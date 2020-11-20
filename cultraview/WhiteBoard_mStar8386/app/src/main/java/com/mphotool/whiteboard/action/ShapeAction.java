package com.mphotool.whiteboard.action;

import android.graphics.Rect;
import android.view.MotionEvent;

import com.mphotool.whiteboard.elements.ArrowMaterial;
import com.mphotool.whiteboard.elements.CircleMaterial;
import com.mphotool.whiteboard.elements.CylinderMaterial;
import com.mphotool.whiteboard.elements.FivePointMaterial;
import com.mphotool.whiteboard.elements.LineMaterial;
import com.mphotool.whiteboard.elements.Material;
import com.mphotool.whiteboard.elements.OvalMaterial;
import com.mphotool.whiteboard.elements.RectangleMaterial;
import com.mphotool.whiteboard.elements.RightTriangleMaterial;
import com.mphotool.whiteboard.elements.ShapeMaterial;
import com.mphotool.whiteboard.elements.TriangleMaterial;
import com.mphotool.whiteboard.utils.Constants;
import com.mphotool.whiteboard.utils.StatusEnum;
import com.mphotool.whiteboard.view.PanelManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 形状action
 * @Author: wanghang
 * @CreateDate: 2019/10/9 18:02
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/10/9 18:02
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class ShapeAction extends Action implements Serializable {
    private static final String TAG = ShapeAction.class.getSimpleName();

    private PanelManager mManager;
    private ShapeMaterial mMaterial;

//    private ShapePathHelper shapePathHelper;

    private Rect mDrawRect = new Rect(); // 绘制区域
    private transient boolean isCanAddMaterial = false; // 能否添加素材

    /**
     * 画笔颜色
     */
    protected int mBrushColor = -1;
    /**
     * 画笔宽度
     */
    protected float mBrushThickness = Constants.PEN_WIDTH_MIDDLE;

    ArrayList<Material> mAfterMaterials = new ArrayList();
    ArrayList<Material> mBeforeMaterials = new ArrayList();
    private transient float mLastX;
    private transient float mLastY;

    public ShapeAction(PanelManager manager){
//        BaseUtils.dbg(TAG, "ShapeAction 初始化");
        this.mManager = manager;
        mMaterial = createMaterial();
    }

    private ShapeAction createAction(PanelManager manager, List<Material> list)
    {
        ShapeAction temp = new ShapeAction(manager);
        temp.materialList.addAll(list);
        return temp;
    }

    @Override
    public void linkMaterial(List<Material> lists) {
        for (Integer i : mMaterialIdList)
        {
            if (i.intValue() >= 0 && i.intValue() < lists.size())
            {
                materialList.add(lists.get(i.intValue()));
            }
        }
    }

    @Override
    public boolean onTouch(PanelManager manager, MotionEvent event) {
        int action = event.getActionMasked();
        float x = event.getX(0);
        float y = event.getY(0);
        float dx = x;
        float dy = y;
        int padding = (int) (5.0f + mBrushThickness);

        // 初始化 mMaterial
        if (mMaterial == null || action == MotionEvent.ACTION_DOWN){
//            BaseUtils.dbg(TAG, "onTouch init ShapeMaterial");
            mMaterial = createMaterial();
            mMaterial.handleTouch(dx, dy, action);
            mDrawRect.set((int)dx, (int)dy, (int)dx, (int)dy);

            mMaterial.showRect(true);
        }

        switch (action){
            case MotionEvent.ACTION_DOWN:{
                isCanAddMaterial = false;

                mLastX = x;
                mLastY = y;
                mMaterial = createMaterial();
                mMaterial.handleTouch(dx, dy, action);
                mDrawRect.set((int) (dx - padding), (int) (dy - padding), (int) (padding + dx), (int) (padding + dy));
                mDrawRect.union((int) x, (int) y);

                mMaterial.showRect(true);
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                mMaterial.showRect(true);
                mDrawRect.union((int)x, (int)y);
                Rect shapeRect = new Rect((int) (dx - padding), (int) (dy - padding), (int) (padding + dx), (int) (padding + dy));

                mDrawRect.union(shapeRect);

                mMaterial.handleTouch(dx, dy, action);

                float detelX = Math.abs(x - mLastX);
                float detelY = Math.abs(y - mLastY);
                double distance = Math.sqrt((double) ((detelX * detelX + detelY * detelY)));
                if (distance > 2.0d){
                    if (!isCanAddMaterial){
                        isCanAddMaterial = true;
                    }
                    manager.repaintScreen(mDrawRect, mMaterial);
                }
                break;
            }
            case MotionEvent.ACTION_UP:{
                mMaterial.showRect(false);
                mMaterial.handleTouch(dx, dy, action);

                // 需要添加素材
                if (isCanAddMaterial){
//                    mMaterial = mMaterial.getCurrentMaterial();

                    manager.addMaterial(mMaterial);

                    materialList.add(mMaterial);
                    ShapeAction action1 = createAction(manager, materialList);
                    manager.addAction(action1);
                    materialList.clear();

                    manager.repaintToOffScreenCanvas(null, true);
                }
                break;
            }
        }

        mLastX = x;
        mLastY = y;
        return true;
    }

    public int getCurrentBrushColor()
    {
        return this.mBrushColor;
    }

    public void setCurrentBrushColor(int color)
    {
        this.mBrushColor = color;
    }

    public float getCurrentBrushThickness()
    {
        return this.mBrushThickness;
    }

    public void setCurrentBrushThickness(float thickness)
    {
        this.mBrushThickness = thickness;
    }

    public void undo(PanelManager manager)
    {
        super.undo(manager);
//        manager.getMaterials().clear();
//        manager.getMaterials().addAll(mBeforeMaterials);

        manager.getMaterials().removeAll(materialList);
    }

    public void redo(PanelManager manager)
    {
        super.redo(manager);
//        manager.getMaterials().clear();
//        manager.getMaterials().addAll(mAfterMaterials);

        manager.getMaterials().addAll(materialList);
    }

    /**
     * 根据类型, 创建素材
     * @return
     */
    public ShapeMaterial createMaterial(){
        ShapeMaterial material;
        final int shapeType = mManager.getShapeType();
        switch (shapeType){
            case StatusEnum.STATUS_SHAPE_RECTTANGLE:{ // 矩形
                material = new RectangleMaterial(mManager, mBrushColor,  mBrushThickness);
                break;
            }
            case StatusEnum.STATUS_SHAPE_LINE:{ // 直线
                material = new LineMaterial(mManager, mBrushColor,  mBrushThickness);
                break;
            }
            case StatusEnum.STATUS_SHAPE_OVAL:{ // 椭圆
                material = new OvalMaterial(mManager, mBrushColor,  mBrushThickness);
                break;
            }
            case StatusEnum.STATUS_SHAPE_CIRCLE:{ // 圆形
                material = new CircleMaterial(mManager, mBrushColor,  mBrushThickness);
                break;
            }
            case StatusEnum.STATUS_SHAPE_TRIANGLE:{// 等腰三角形
                material = new TriangleMaterial(mManager, mBrushColor,  mBrushThickness);
                break;
            }
            case StatusEnum.STATUS_SHAPE_RIGHT_TRIANGLE:{ // 直角三角形
                material = new RightTriangleMaterial(mManager, mBrushColor,  mBrushThickness);
                break;
            }
            case StatusEnum.STATUS_SHAPE_ARROW:{ // 左右箭头
                material = new ArrowMaterial(mManager, mBrushColor,  mBrushThickness);
                break;
            }
            case StatusEnum.STATUS_SHAPE_FIVE_POINT:{ // 五角星
                material = new FivePointMaterial(mManager, mBrushColor,  mBrushThickness);
                break;
            }
            case StatusEnum.STATUS_SHAPE_CONE:{
                material = new CylinderMaterial(mManager, mBrushColor,  mBrushThickness);
                break;
            }
            default:{
                material = new RectangleMaterial(mManager, mBrushColor,  mBrushThickness);
            }
        }

//        BaseUtils.dbg(TAG, "createMaterial shapeType:" + shapeType);
        return material;
    }
}
