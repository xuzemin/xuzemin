local function DEFAULT_STYLE()
  return {
    ['2d'] = {
      style = 'polygon',
      face = {
        color = '0xffe1e9ef',
        enable_alpha = false,
        texture = null,
        automatic_scale = null
      },
      outline = {
        color = '0xffc0c0c0',
        width = 0.02,
        enable_alpha = false,
      },
      left_side = {}
     }
	 }
  end
  

local function DEFAULT_3D_STYLE()
  return{
  ['2d'] = {
      style = 'polygon',
      face = {
        color = '0xffe1e9ef',
        enable_alpha = false,
        texture = null,
        automatic_scale = null
      },
      outline = {
        color = '0xffc0c0c0',
        width = 0.02,
        enable_alpha = false,
      },
      left_side = {}
     },
    ['3d'] = {
      style = 'polygon',
      face_on_bottom = false, --为false时 height才有效
      height = 2, --如果多边形有面的话，要和outline的高度相同
      face = {
        color = '0Xfffff5ee',
        enable_alpha = false
      },
      outline = {
        color = '0XFF000000',
        width = 0.05,	
        height = 2,
		enable_alpha=false,
        left_side = {
          color = '0XFFeed3c1',
		  enable_alpha = false
        },
        right_side = {
          color = '0XFFeed3c1',
		  enable_alpha = false
        },
        top_side = {
          color = '0XFF000000',
		  enable_alpha = false
        }
      }
    } 
  }
  end

local function COLOR_STYLE(a, b, c)
  style = DEFAULT_STYLE()
  style['2d'].face.color = a;
  style['2d'].face.enable_alpha = false;
  style['2d'].outline.color = b or '0xFFc0c0c0';
  style['2d'].outline.width = c or 0.02;
  style['2d'].outline.enable_alpha = false;
  return style
end

local function COLOR_3D_STYLE(a,b,c)
  style = DEFAULT_3D_STYLE()
  style['2d'].face.color = a;
  style['2d'].face.enable_alpha = false;
  style['2d'].outline.color = b or '0xFFc0c0c0';
  style['2d'].outline.width = c or 0.02;
  style['2d'].outline.enable_alpha = false;
  return style
end



local function TEXTURE_1_STYLE(a, b, c)
  style = DEFAULT_STYLE()
  style['2d'].face.color = null;
  style['2d'].face.enable_alpha = true;
  style['2d'].face.texture = a;
  style['2d'].outline.color = b or '0xff7D7D7D';
  style['2d'].face.automatic_scale = true;
  style['2d'].outline.width = c or 0.1;
  return style
end

local function TEXTURE_2_STYLE(a, b, c)
  style = DEFAULT_STYLE()
  style['2d'].face.color = null;
  style['2d'].face.enable_alpha = true;
  style['2d'].face.texture = a;
  style['2d'].outline.color = b or '0xff7D7D7D';
  style['2d'].face.automatic_scale = false;
  style['2d'].outline.width = c or 0.1;
  return style
end

local function DEFAULT_ICON()
  return {
    ['2d'] = {
      style = 'icon',
      icon = "icons/00000000.png",
      use_texture_origin_size = false,
      width = 45,
      height = 45,
      anchor_x = 0.5,
      anchor_y = 0.5
    }
  }
end

local function ICON(a)
  return {
    ['2d'] = {
      style = 'icon',
      icon = a,
      use_texture_origin_size = false,
      width = 45,
      height = 45,
      anchor_x = 0.5,
      anchor_y = 0.5
    }
  }
end

CONFIG = {
  views = {
    default = {
      layers = {
        Frame = {
          height_offset = 0.1,
          renderer = {
            type = 'simple',
            ['2d'] = {
              style = 'polygon',
              face = {
                color = '0xffffffff',
                enable_alpha = false,
              },
              outline = {
                color = '0xffC0C0C0',
                width = 0.05,
                enable_alpha = true,
              },
              left_side = {}
            },
--            ['3d'] = {
--              style = 'polygon',
--              height = 200;
--              face = {
--                color = '0xffffffff',
--                enable_alpha = false
--              },
--              outline = {
--                color = '0xff000000',
--                width = 1,
--                left_side = {
--                  color = '0xff898989'
--                },
--                right_side = {
--                  color = '0xff898989'
--                },
--                top_side = {
--                  color = '0xff97867d'
--                }
--              }
--            }
          }
        },
        Area = {
          height_offset = 0,
          renderer = {
            type = 'unique',
            key = {
              'id',
              'category',
            },
            default = DEFAULT_STYLE(),
            styles = {
              -- 上海证券大厦
			  --0xffA1C4ED
			  [26647]    = COLOR_STYLE('0xffA1c4ed'),
              [24001000] = {
			  ['2d'] = {
      style = 'polygon',
      face = {
        color = '0xff9fbff0',
        enable_alpha = false,
        texture = null,
        automatic_scale = null
      },
      outline = {
        color = '0xffababab',
        width = 0.03,
        enable_alpha = false,
      },
      left_side = {}
     },
    ['3d'] = {
      style = 'polygon',
      face_on_bottom = false, --为false时 height才有效
      height = 0.5, --如果多边形有面的话，要和outline的高度相同
      face = {
        color = '0Xff9ebfef',
        enable_alpha = false
      },
      outline = {
        color = '0XFFababab',
        width = 0.03,
        height = 0.5,
		enable_alpha=false,
        left_side = {
          color = '0XFF7788a9',
		  enable_alpha = false
        },
        right_side = {
          color = '0XFF7788a9',
		  enable_alpha = false
        },
        top_side = {
          color = '0XFFababab',
		  enable_alpha = false
        }
      }
    }
			  },--办公桌
              [24004000] = COLOR_STYLE('0xff7c99d1'), -- 办公椅
              [23027000] = {
			  ['2d'] = {
      style = 'polygon',
      face = {
        color = '0xffc1e0fc',
        enable_alpha = false,
        texture = null,
        automatic_scale = null
      },
      outline = {
        color = '0xffababab',
        width = 0.03,
        enable_alpha = false,
      },
      left_side = {}
     },
    ['3d'] = {
      style = 'polygon',
      face_on_bottom = false, --为false时 height才有效
      height = 2, --如果多边形有面的话，要和outline的高度相同
      face = {
        color = '0Xffd8eafb',
        enable_alpha = false
      },
      outline = {
        color = '0XFFababab',
        width = 0.03,
        height = 2,
		enable_alpha=false,
        left_side = {
          color = '0XFF94a5c5',
		  enable_alpha = false
        },
        right_side = {
          color = '0XFF94a5c5',
		  enable_alpha = false
        },
        top_side = {
          color = '0XFFababab',
		  enable_alpha = false
        }
      }
    }
			  }, -- 办公室
              [14000000] = {
			  ['2d'] = {
      style = 'polygon',
      face = {
        color = '0xfff2f1ed',
        enable_alpha = false,
        texture = null,
        automatic_scale = null
      },
      outline = {
        color = '0xffababab',
        width = 0.03,
        enable_alpha = false,
      },
      left_side = {}
     },
    ['3d'] = {
      style = 'polygon',
      face_on_bottom = false, --为false时 height才有效
      height = 2, --如果多边形有面的话，要和outline的高度相同
      face = {
        color = '0Xffd8eafb',
        enable_alpha = false
      },
      outline = {
        color = '0XFFababab',
        width = 0.03,
        height = 2,
		enable_alpha=false,
        left_side = {
          color = '0XFF94a5c5',
		  enable_alpha = false
        },
        right_side = {
          color = '0XFF94a5c5',
		  enable_alpha = false
        },
        top_side = {
          color = '0XFFababab',
		  enable_alpha = false
        }
      }
    }
			  }, -- 乒乓球室
              [23024000] = {
			  ['2d'] = {
      style = 'polygon',
      face = {
        color = '0xfff5f1ce',
        enable_alpha = false,
        texture = null,
        automatic_scale = null
      },
      outline = {
        color = '0xffababab',
        width = 0.03,
        enable_alpha = false,
      },
      left_side = {}
     },
    ['3d'] = {
      style = 'polygon',
      face_on_bottom = false, --为false时 height才有效
      height = 2, --如果多边形有面的话，要和outline的高度相同
      face = {
        color = '0Xfff5f1ce',
        enable_alpha = false
      },
      outline = {
        color = '0XFFababab',
        width = 0.03,
        height = 2,
		enable_alpha=false,
        left_side = {
          color = '0XFFb2ae90',
		  enable_alpha = false
        },
        right_side = {
          color = '0XFFb2ae90',
		  enable_alpha = false
        },
        top_side = {
          color = '0XFFababab',
		  enable_alpha = false
        }
      }
    }
			  },--cesuo
              [24099000] = COLOR_3D_STYLE('0xffc6d6c7'),
              [23025000] = {
			  ['2d'] = {
      style = 'polygon',
      face = {
        color = '0xfff5f1ce',
        enable_alpha = false,
        texture = null,
        automatic_scale = null
      },
      outline = {
        color = '0xffababab',
        width = 0.03,
        enable_alpha = false,
      },
      left_side = {}
     },
    ['3d'] = {
      style = 'polygon',
      face_on_bottom = false, --为false时 height才有效
      height = 2, --如果多边形有面的话，要和outline的高度相同
      face = {
        color = '0Xfff5f1ce',
        enable_alpha = false
      },
      outline = {
        color = '0XFFababab',
        width = 0.03,
        height = 2,
		enable_alpha=false,
        left_side = {
          color = '0XFFb2ae90',
		  enable_alpha = false
        },
        right_side = {
          color = '0XFFb2ae90',
		  enable_alpha = false
        },
        top_side = {
          color = '0XFFababab',
		  enable_alpha = false
        }
      }
    }
			  },--cesuo
              [24097000] = COLOR_STYLE('0xffefd7bf'),--louti
              [00000000] = COLOR_3D_STYLE('0xff6392A1'),
              [24091000] = {
			  ['2d'] = {
      style = 'polygon',
      face = {
        color = '0xfff3e4f9',
        enable_alpha = false,
        texture = null,
        automatic_scale = null
      },
      outline = {
        color = '0xffababab',
        width = 0.03,
        enable_alpha = false,
      },
      left_side = {}
     },
    ['3d'] = {
      style = 'polygon',
      face_on_bottom = false, --为false时 height才有效
      height = 1, --如果多边形有面的话，要和outline的高度相同
      face = {
        color = '0xfff3e4f9',
        enable_alpha = false
      },
      outline = {
        color = '0XFFababab',
        width = 0.03,
        height = 1,
		enable_alpha=false,
        left_side = {
          color = '0XFFcfc1d4',
		  enable_alpha = false
        },
        right_side = {
          color = '0XFFcfc1d4',
		  enable_alpha = false
        },
        top_side = {
          color = '0XFFababab',
		  enable_alpha = false
        }
      }
    }
			  },--dianti
            }
          }
        },
        Area_text = {
          collision_detection = true,
          font_path = "/storage/emulated/0/Nagrand/lua/DroidSansFallback.ttf",
          renderer = {
            type = 'simple',
            ['2d'] = {
              style = 'annotation',
              color = '0xFF696969',
			  field = 'name',
			  --field = '#category##name#',
              size = 30,
              outline_color = '0xFF000000',
              outline_width = 0,
              anchor_x = 0.5,
              anchor_y = 0.5
            },
          }
        },
        Facility = {
          height_offset = -0.2;
          collision_detection = true,
          renderer = {
            type = 'unique',
            key = {
              'category'
            },
            default = DEFAULT_ICON(),
            styles = {
              [11000000] = ICON('icons/11000000.png'),
              [11401000] = ICON('icons/11401000.png'),
              [11454000] = ICON('icons/11454000.png'),
              [13076000] = ICON('icons/13076000.png'),
              [13113000] = ICON('icons/13113000.png'),
              [13116000] = ICON('icons/13116000.png'),
              [15001000] = ICON('icons/15001000.png'),
              [15002000] = ICON('icons/15002000.png'),
              [15026000] = ICON('icons/15026000.png'),
              [15043000] = ICON('icons/15043000.png'),
              [15044000] = ICON('icons/15044000.png'),
              [17001000] = ICON('icons/17001000.png'),
              [17004000] = ICON('icons/17004000.png'),
              [17006000] = ICON('icons/17006000.png'),
              [17007000] = ICON('icons/17007000.png'),
              [17008000] = ICON('icons/17008000.png'),
              [21048000] = ICON('icons/21048000.png'),
              [21049000] = ICON('icons/21049000.png'),
              [22011000] = ICON('icons/22011000.png'),
              [22012000] = ICON('icons/22012000.png'),
              [22014000] = ICON('icons/22014000.png'),
              [22015000] = ICON('icons/22015000.png'),
              [22016000] = ICON('icons/22016000.png'),
              [22017000] = ICON('icons/22017000.png'),
              [22019000] = ICON('icons/22019000.png'),
              [22021000] = ICON('icons/22021000.png'),
              [22022000] = ICON('icons/22022000.png'),
              [22023000] = ICON('icons/22023000.png'),
              [22033000] = ICON('icons/22033000.png'),
              [22039000] = ICON('icons/22039000.png'),
              [22040000] = ICON('icons/22040000.png'),
              [22052000] = ICON('icons/22052000.png'),
              [22053000] = ICON('icons/22053000.png'),
              [22054000] = ICON('icons/22054000.png'),
              [22055000] = ICON('icons/22055000.png'),
              [23004000] = ICON('icons/23004000.png'),
              [23005000] = ICON('icons/23005000.png'),
              [23007000] = ICON('icons/23007000.png'),
              [23008000] = ICON('icons/23008000.png'),
              [23009000] = ICON('icons/23009000.png'),
              [23010000] = ICON('icons/23010000.png'),
              [23011000] = ICON('icons/23011000.png'),
              [23012000] = ICON('icons/23012000.png'),
              [23013000] = ICON('icons/23013000.png'),
              [23014000] = ICON('icons/23014000.png'),
              [23015000] = ICON('icons/23015000.png'),
              [23016000] = ICON('icons/23016000.png'),
              [23017000] = ICON('icons/23017000.png'),
              [23018000] = ICON('icons/23018000.png'),
              [23019000] = ICON('icons/23019000.png'),
              [23020000] = ICON('icons/23020000.png'),
              [23021000] = ICON('icons/23021000.png'),
              [23022000] = ICON('icons/23022000.png'),
              [23023000] = ICON('icons/23023000.png'),
              [23024000] = ICON('icons/23024000.png'),
              [23025000] = ICON('icons/23025000.png'),
              [23026000] = ICON('icons/23026000.png'),
              [23027000] = ICON('icons/23027000.png'),
              [23028000] = ICON('icons/23028000.png'),
              [23029000] = ICON('icons/23029000.png'),
              [23030000] = ICON('icons/23030000.png'),
              [23031000] = ICON('icons/23031000.png'),
              [23032000] = ICON('icons/23032000.png'),
              [23033000] = ICON('icons/23033000.png'),
              [23034000] = ICON('icons/23034000.png'),
              [23035000] = ICON('icons/23035000.png'),
              [23036000] = ICON('icons/23036000.png'),
              [23037000] = ICON('icons/23037000.png'),
              [23038000] = ICON('icons/23038000.png'),
              [23039000] = ICON('icons/23039000.png'),
              [23040000] = ICON('icons/23040000.png'),
              [23041000] = ICON('icons/23041000.png'),
              [23042000] = ICON('icons/23042000.png'),
              [23059000] = ICON('icons/23059000.png'),
              [23060000] = ICON('icons/23060000.png'),
              [23061000] = ICON('icons/23061000.png'),
              [24003000] = ICON('icons/24003000.png'),
              [24006000] = ICON('icons/24006000.png'),
              [24014000] = ICON('icons/24014000.png'),
              [24091000] = ICON('icons/24091000.png'),
              [24092000] = ICON('icons/24092000.png'),
              [24093000] = ICON('icons/24093000.png'),
              [24094000] = ICON('icons/24094000.png'),
              [24097000] = ICON('icons/24097000.png'),
              [24098000] = ICON('icons/24098000.png'),
              [24099000] = ICON('icons/24099000.png'),
              [24100000] = ICON('icons/24100000.png'),
              [24111000] = ICON('icons/24111000.png'),
              [24112000] = ICON('icons/24112000.png'),
              [24113000] = ICON('icons/24113000.png'),
              [24114000] = ICON('icons/24114000.png'),
              [24115000] = ICON('icons/24115000.png'),
              [24116000] = ICON('icons/24116000.png'),
              [24117000] = ICON('icons/24117000.png'),
              [24118000] = ICON('icons/24118000.png'),
              [24119000] = ICON('icons/24119000.png'),
              [24120000] = ICON('icons/24120000.png'),
              [24121000] = ICON('icons/24121000.png'),
              [24141000] = ICON('icons/24141000.png'),
              [24142000] = ICON('icons/24142000.png'),
              [24151000] = ICON('icons/24151000.png'),
              [24152000] = ICON('icons/24152000.png'),
              [24161000] = ICON('icons/24161000.png'),
              [24162000] = ICON('icons/24162000.png'),
              [24163000] = ICON('icons/24163000.png'),
              [34001000] = ICON('icons/34001000.png'),
              [34002000] = ICON('icons/34002000.png'),
              [35001000] = ICON('icons/35001000.png'),
            }
          }
        },
        positioning = {
          height_offset = - 0.4,
          renderer = {
            type = 'simple',
            ['2d'] = {
              style = 'icon',
--              style = 'color_point',
--              color = '0xFF006699',
              icon = 'mapping/location.png',
              enable_alpha = true,
--              size = 3,
            },
          }
        },
        navigate = {
          height_offset = - 0.3,
		  collision_detection=true;
          renderer = {
            type = 'simple',
            ['2d'] = {
              style = 'linestring',
              color = '0xFF698AE7',
              line_style = 'SOLID',
              enable_alpha = true,
              width = 0.3,
			  has_start=true,
			  has_end=true,
			  has_arrow=true,
            },
          }
        }
      }
    },
	color_red = { -- 地图功能之颜色: red
      layers = {
        Frame = {
          height_offset = 0.1,
          renderer = {
            type = 'simple',
            ['2d'] = {
              style = 'polygon',
              face = {
                color = '0xffFFFFD9',
                enable_alpha = false,
              },
              outline = {
                color = '0xff000000',
                width = 0.3,
                enable_alpha = true,
              },
              left_side = {}
            },
          }
        },
        Area = {
          height_offset = 0,
          renderer = {
            type = 'unique',
            key = {
              'id',
              'category',
            },
            default = DEFAULT_STYLE(),
            styles = {
              [953562] = COLOR_STYLE('0xffff0000'), -- 这里是用shopId
              -- 办公桌
              [24001000] = COLOR_STYLE('0xff00ffff', null, 0.1),
              -- 办公椅
              [24004000] = COLOR_STYLE('0xff00ffff', null, 0.1),
              -- 椅子
              [24005000] = COLOR_STYLE('0xff00ffff', null, 0.1),
              -- 桌子
              [24002000] = COLOR_STYLE('0xff00ffff', null, 0.1),
            }
          }
        },
        Area_text = {
          collision_detection = true,
          font_path = "/storage/emulated/0/Nagrand/lua/DroidSansFallback.ttf",
          renderer = {
            type = 'simple',
            ['2d'] = {
              style = 'annotation',
              color = '0xFF696969',
              field = 'name',
              size = 30,
              outline_color = '0xFF000000',
              outline_width = 0,
              anchor_x = 0.5,
              anchor_y = 0.5
            },
          }
        },
      }
    },
    color_green = { -- 地图功能之颜色: green
      layers = {
        Frame = {
          height_offset = 0.1,
          renderer = {
            type = 'simple',
            ['2d'] = {
              style = 'polygon',
              face = {
                color = '0xffFFFFD9',
                enable_alpha = false,
              },
              outline = {
                color = '0xff000000',
                width = 0.3,
                enable_alpha = true,
              },
              left_side = {}
            },
          }
        },
        Area = {
          height_offset = 0,
          renderer = {
            type = 'unique',
            key = {
              'id',
              'category',
            },
            default = DEFAULT_STYLE(),
            styles = {
              [953562] = COLOR_STYLE('0xff00ff00'), -- 这里是用shopId
              -- 办公桌
              [24001000] = COLOR_STYLE('0xff00ffff', null, 0.1),
              -- 办公椅
              [24004000] = COLOR_STYLE('0xff00ffff', null, 0.1),
              -- 椅子
              [24005000] = COLOR_STYLE('0xff00ffff', null, 0.1),
              -- 桌子
              [24002000] = COLOR_STYLE('0xff00ffff', null, 0.1),
            }
          }
        },
        Area_text = {
          collision_detection = true,
          font_path = "/storage/emulated/0/Nagrand/lua/DroidSansFallback.ttf",
          renderer = {
            type = 'simple',
            ['2d'] = {
              style = 'annotation',
              color = '0xFF696969',
              field = 'name',
              size = 30,
              outline_color = '0xFF000000',
              outline_width = 0,
              anchor_x = 0.5,
              anchor_y = 0.5
            },
          }
        },
      }
    },
    color_blue = { -- 地图功能之颜色: blue
      layers = {
        Frame = {
          height_offset = 0.1,
          renderer = {
            type = 'simple',
            ['2d'] = {
              style = 'polygon',
              face = {
                color = '0xffFFFFD9',
                enable_alpha = false,
              },
              outline = {
                color = '0xff000000',
                width = 0.3,
                enable_alpha = true,
              },
              left_side = {}
            },
          }
        },
        Area = {
          height_offset = 0,
          renderer = {
            type = 'unique',
            key = {
              'id',
              'category',
            },
            default = DEFAULT_STYLE(),
            styles = {
              [953562] = COLOR_STYLE('0xff0000ff'), -- 这里是用shopId
              -- 办公桌
              [24001000] = COLOR_STYLE('0xff00ffff', null, 0.1),
              -- 办公椅
              [24004000] = COLOR_STYLE('0xff00ffff', null, 0.1),
              -- 椅子
              [24005000] = COLOR_STYLE('0xff00ffff', null, 0.1),
              -- 桌子
              [24002000] = COLOR_STYLE('0xff00ffff', null, 0.1),
            }
          }
        },
        Area_text = {
          collision_detection = true,
          font_path = "/storage/emulated/0/Nagrand/lua/DroidSansFallback.ttf",
          renderer = {
            type = 'simple',
            ['2d'] = {
              style = 'annotation',
              color = '0xFF696969',
              field = 'name',
              size = 30,
              outline_color = '0xFF000000',
              outline_width = 0,
              anchor_x = 0.5,
              anchor_y = 0.5
            },
          }
        },
      }
    },
    texture_1 = { -- 地图功能之纹理1
      layers = {
        Frame = {
          height_offset = 0.1,
          renderer = {
            type = 'simple',
            ['2d'] = {
              style = 'polygon',
              face = {
                texture = 'tile/frame_1.jpg',
                enable_alpha = false,
                auto_scale = true
              },
              outline = {
                color = '0xff000000',
                width = 0.3,
                enable_alpha = true,
              },
              left_side = {}
            },
          }
        },
        Area = {
          height_offset = 0,
          renderer = {
            type = 'unique',
            key = {
              'id',
              'category',
            },
            default = {
              style = 'polygon',
              face = {
                enable_alpha = true,
                texture = 'tile/kefang_2.jpg',
                automatic_scale = false
              },
              outline = {
                color = '0xff333333',
                width = 0.2,
                enable_alpha = true,
              },
              left_side = {}
            },
            styles = {
              [953562] = TEXTURE_1_STYLE('mapping/25109.png'), -- 平铺方式
              -- 办公桌
              [24001000] = TEXTURE_2_STYLE('tile/stone_0005.jpg'),
              -- 办公椅
              [24004000] = TEXTURE_2_STYLE('tile/stone_0005.jpg'),
              -- 椅子
              [24005000] = TEXTURE_2_STYLE('tile/stone_0005.jpg'),
              -- 桌子
              [24002000] = TEXTURE_2_STYLE('tile/stone_0005.jpg'),
            }
          }
        },
        Area_text = {
          collision_detection = true,
          font_path = "/storage/emulated/0/Nagrand/lua/DroidSansFallback.ttf",
          renderer = {
            type = 'simple',
            ['2d'] = {
              style = 'annotation',
              color = '0xFF696969',
              field = 'name',
              size = 30,
              outline_color = '0xFF000000',
              outline_width = 0,
              anchor_x = 0.5,
              anchor_y = 0.5
            },
          }
        },
      }
    },
    texture_2 = { -- 地图功能之纹理2
      layers = {
        Frame = {
          height_offset = 0.1,
          renderer = {
            type = 'simple',
            ['2d'] = {
              style = 'polygon',
              face = {
                texture = 'tile/frame_1.jpg',
                enable_alpha = false,
                auto_scale = true
              },
              outline = {
                color = '0xff000000',
                width = 0.3,
                enable_alpha = true,
              },
              left_side = {}
            },
          }
        },
        Area = {
          height_offset = 0,
          renderer = {
            type = 'unique',
            key = {
              'id',
              'category',
            },
            default = {
              style = 'polygon',
              face = {
                enable_alpha = true,
                texture = 'tile/kefang_2.jpg',
                automatic_scale = false
              },
              outline = {
                color = '0xff333333',
                width = 0.2,
                enable_alpha = true,
              },
              left_side = {}
            },
            styles = {
              [953562] = TEXTURE_1_STYLE('tile/room_3.jpg'), -- 平铺方式
              -- 办公桌
              [24001000] = TEXTURE_2_STYLE('tile/stone_0005.jpg'), -- 缩放方式
              -- 办公椅
              [24004000] = TEXTURE_2_STYLE('tile/stone_0005.jpg'),
              -- 椅子
              [24005000] = TEXTURE_2_STYLE('tile/stone_0005.jpg'),
              -- 桌子
              [24002000] = TEXTURE_2_STYLE('tile/stone_0005.jpg'),
            }
          }
        },
        Area_text = {
          collision_detection = true,
          font_path = "/storage/emulated/0/Nagrand/lua/DroidSansFallback.ttf",
          renderer = {
            type = 'simple',
            ['2d'] = {
              style = 'annotation',
              color = '0xFF696969',
              field = 'name',
              size = 30,
              outline_color = '0xFF000000',
              outline_width = 0,
              anchor_x = 0.5,
              anchor_y = 0.5
            },
          }
        },
      }
    },
    texture_3 = { -- 地图功能之纹理3
      layers = {
        Frame = {
          height_offset = 0.1,
          renderer = {
            type = 'simple',
            ['2d'] = {
              style = 'polygon',
              face = {
                texture = 'tile/frame_1.jpg',
                enable_alpha = false,
                auto_scale = true
              },
              outline = {
                color = '0xff000000',
                width = 0.3,
                enable_alpha = true,
              },
              left_side = {}
            },
          }
        },
        Area = {
          height_offset = 0,
          renderer = {
            type = 'unique',
            key = {
              'id',
              'category',
            },
            default = {
              style = 'polygon',
              face = {
                enable_alpha = true,
                texture = 'tile/kefang_2.jpg',
                automatic_scale = false
              },
              outline = {
                color = '0xff333333',
                width = 0.2,
                enable_alpha = true,
              },
              left_side = {}
            },
            styles = {
              [953562] = TEXTURE_1_STYLE('tile/grass_1.png'),
              -- 办公桌
              [24001000] = TEXTURE_2_STYLE('tile/stone_0005.jpg'),
              -- 办公椅
              [24004000] = TEXTURE_2_STYLE('tile/stone_0005.jpg'),
              -- 椅子
              [24005000] = TEXTURE_2_STYLE('tile/stone_0005.jpg'),
              -- 桌子
              [24002000] = TEXTURE_2_STYLE('tile/stone_0005.jpg'),
            }
          }
        },
        Area_text = {
          collision_detection = true,
          font_path = "/storage/emulated/0/Nagrand/lua/DroidSansFallback.ttf",
          renderer = {
            type = 'simple',
            ['2d'] = {
              style = 'annotation',
              color = '0xFF696969',
              field = 'name',
              size = 30,
              outline_color = '0xFF000000',
              outline_width = 0,
              anchor_x = 0.5,
              anchor_y = 0.5
            },
          }
        },
      }
    },
    typeface_1 = { -- 地图功能之字体1
      layers = {
        Frame = {
          height_offset = 0.1,
          renderer = {
            type = 'simple',
            ['2d'] = {
              style = 'polygon',
              face = {
                color = '0xffFFFFD9',
                enable_alpha = false,
              },
              outline = {
                color = '0xff000000',
                width = 0.3,
                enable_alpha = true,
              },
              left_side = {}
            },
          }
        },
        Area = {
          height_offset = 0,
          renderer = {
            type = 'unique',
            key = {
              'id',
              'category',
            },
            default = DEFAULT_STYLE(),
            styles = {
              -- 办公桌
              [24001000] = COLOR_STYLE('0xff00ffff', null, 0.1),
              -- 办公椅
              [24004000] = COLOR_STYLE('0xff00ffff', null, 0.1),
              -- 椅子
              [24005000] = COLOR_STYLE('0xff00ffff', null, 0.1),
              -- 桌子
              [24002000] = COLOR_STYLE('0xff00ffff', null, 0.1),
            }
          }
        },
        Area_text = {
          collision_detection = true,
          font_path = "/storage/emulated/0/Nagrand/lua/DroidSansFallback.ttf",
          renderer = {
            type = 'simple',
            ['2d'] = {
              style = 'annotation',
              color = '0xFF696969',
              field = 'name',
              size = 30,
              outline_color = '0xFF000000',
              outline_width = 0,
              anchor_x = 0.5,
              anchor_y = 0.5
            },
          }
        }
      }
    },
    typeface_2 = { -- 地图功能之字体2
      layers = {
        Frame = {
          height_offset = 0.1,
          renderer = {
            type = 'simple',
            ['2d'] = {
              style = 'polygon',
              face = {
                color = '0xffFFFFD9',
                enable_alpha = false,
              },
              outline = {
                color = '0xff000000',
                width = 0.3,
                enable_alpha = true,
              },
              left_side = {}
            },
          }
        },
        Area = {
          height_offset = 0,
          renderer = {
            type = 'unique',
            key = {
              'id',
              'category',
            },
            default = DEFAULT_STYLE(),
            styles = {
              -- 办公桌
              [24001000] = COLOR_STYLE('0xff00ffff', null, 0.1),
              -- 办公椅
              [24004000] = COLOR_STYLE('0xff00ffff', null, 0.1),
              -- 椅子
              [24005000] = COLOR_STYLE('0xff00ffff', null, 0.1),
              -- 桌子
              [24002000] = COLOR_STYLE('0xff00ffff', null, 0.1),
            }
          }
        },
        Area_text = {
          collision_detection = true,
          font_path = "/storage/emulated/0/Nagrand/lua/simfang.ttf",
          renderer = {
            type = 'simple',
            ['2d'] = {
              style = 'annotation',
              color = '0xFF696969',
              field = 'name',
              size = 30,
              outline_color = '0xFF000000',
              outline_width = 0,
              anchor_x = 0.5,
              anchor_y = 0.5
            },
          }
        }
      }
    },
    publicService = { -- 地图功能之公共设施
      layers = {
        Frame = {
          height_offset = 0.1,
          renderer = {
            type = 'simple',
            ['2d'] = {
              style = 'polygon',
              face = {
                color = '0xffFFFFD9',
                enable_alpha = false,
              },
              outline = {
                color = '0xff000000',
                width = 0.3,
                enable_alpha = true,
              },
              left_side = {}
            },
          }
        },
        Area = {
          height_offset = 0,
          renderer = {
            type = 'unique',
            key = {
              'id',
              'category',
            },
            default = DEFAULT_STYLE(),
            styles = {
              -- 办公桌
              [24001000] = COLOR_STYLE('0xff00ffff', null, 0.1),
              -- 办公椅
              [24004000] = COLOR_STYLE('0xff00ffff', null, 0.1),
              -- 椅子
              [24005000] = COLOR_STYLE('0xff00ffff', null, 0.1),
              -- 桌子
              [24002000] = COLOR_STYLE('0xff00ffff', null, 0.1),
            }
          }
        },
        Area_text = {
          collision_detection = true,
          font_path = "/storage/emulated/0/Nagrand/lua/DroidSansFallback.ttf",
          renderer = {
            type = 'simple',
            ['2d'] = {
              style = 'annotation',
              color = '0xFF696969',
              field = 'name',
              size = 30,
              outline_color = '0xFF000000',
              outline_width = 0,
              anchor_x = 0.5,
              anchor_y = 0.5
            },
          }
        },
        Facility = {
          collision_detection = true,
          height_offset = -0.2;
          renderer = {
            type = 'unique',
            key = {
              'id',
              'category'
            },
            default = DEFAULT_ICON(),
            styles = {
              [11000000] = ICON('icons/11000000.png'),
              [11401000] = ICON('icons/11401000.png'),
              [11454000] = ICON('icons/11454000.png'),
              [13076000] = ICON('icons/13076000.png'),
              [13113000] = ICON('icons/13113000.png'),
              [13116000] = ICON('icons/13116000.png'),
              [15001000] = ICON('icons/15001000.png'),
              [15002000] = ICON('icons/15002000.png'),
              [15026000] = ICON('icons/15026000.png'),
              [15043000] = ICON('icons/15043000.png'),
              [15044000] = ICON('icons/15044000.png'),
              [17001000] = ICON('icons/17001000.png'),
              [17004000] = ICON('icons/17004000.png'),
              [17006000] = ICON('icons/17006000.png'),
              [17007000] = ICON('icons/17007000.png'),
              [17008000] = ICON('icons/17008000.png'),
              [21048000] = ICON('icons/21048000.png'),
              [21049000] = ICON('icons/21049000.png'),
              [22011000] = ICON('icons/22011000.png'),
              [22012000] = ICON('icons/22012000.png'),
              [22014000] = ICON('icons/22014000.png'),
              [22015000] = ICON('icons/22015000.png'),
              [22016000] = ICON('icons/22016000.png'),
              [22017000] = ICON('icons/22017000.png'),
              [22019000] = ICON('icons/22019000.png'),
              [22021000] = ICON('icons/22021000.png'),
              [22022000] = ICON('icons/22022000.png'),
              [22023000] = ICON('icons/22023000.png'),
              [22033000] = ICON('icons/22033000.png'),
              [22039000] = ICON('icons/22039000.png'),
              [22040000] = ICON('icons/22040000.png'),
              [22052000] = ICON('icons/22052000.png'),
              [22053000] = ICON('icons/22053000.png'),
              [22054000] = ICON('icons/22054000.png'),
              [22055000] = ICON('icons/22055000.png'),
              [23004000] = ICON('icons/23004000.png'),
              [23005000] = ICON('icons/23005000.png'),
              [23007000] = ICON('icons/23007000.png'),
              [23008000] = ICON('icons/23008000.png'),
              [23009000] = ICON('icons/23009000.png'),
              [23010000] = ICON('icons/23010000.png'),
              [23011000] = ICON('icons/23011000.png'),
              [23012000] = ICON('icons/23012000.png'),
              [23013000] = ICON('icons/23013000.png'),
              [23014000] = ICON('icons/23014000.png'),
              [23015000] = ICON('icons/23015000.png'),
              [23016000] = ICON('icons/23016000.png'),
              [23017000] = ICON('icons/23017000.png'),
              [23018000] = ICON('icons/23018000.png'),
              [23019000] = ICON('icons/23019000.png'),
              [23020000] = ICON('icons/23020000.png'),
              [23021000] = ICON('icons/23021000.png'),
              [23022000] = ICON('icons/23022000.png'),
              [23023000] = ICON('icons/23023000.png'),
              [23024000] = ICON('icons/23024000.png'),
              [23025000] = ICON('icons/23025000.png'),
              [23026000] = ICON('icons/23026000.png'),
              [23027000] = ICON('icons/23027000.png'),
              [23028000] = ICON('icons/23028000.png'),
              [23029000] = ICON('icons/23029000.png'),
              [23030000] = ICON('icons/23030000.png'),
              [23031000] = ICON('icons/23031000.png'),
              [23032000] = ICON('icons/23032000.png'),
              [23033000] = ICON('icons/23033000.png'),
              [23034000] = ICON('icons/23034000.png'),
              [23035000] = ICON('icons/23035000.png'),
              [23036000] = ICON('icons/23036000.png'),
              [23037000] = ICON('icons/23037000.png'),
              [23038000] = ICON('icons/23038000.png'),
              [23039000] = ICON('icons/23039000.png'),
              [23040000] = ICON('icons/23040000.png'),
              [23041000] = ICON('icons/23041000.png'),
              [23042000] = ICON('icons/23042000.png'),
              [23059000] = ICON('icons/23059000.png'),
              [23060000] = ICON('icons/23060000.png'),
              [23061000] = ICON('icons/23061000.png'),
              [24003000] = ICON('icons/24003000.png'),
              [24006000] = ICON('icons/24006000.png'),
              [24014000] = ICON('icons/24014000.png'),
              [24091000] = ICON('icons/24091000.png'),
              [24092000] = ICON('icons/24092000.png'),
              [24093000] = ICON('icons/24093000.png'),
              [24094000] = ICON('icons/24094000.png'),
              [24097000] = ICON('icons/24097000.png'),
              [24098000] = ICON('icons/24098000.png'),
              [24099000] = ICON('icons/24099000.png'),
              [24100000] = ICON('icons/24100000.png'),
              [24111000] = ICON('icons/24111000.png'),
              [24112000] = ICON('icons/24112000.png'),
              [24113000] = ICON('icons/24113000.png'),
              [24114000] = ICON('icons/24114000.png'),
              [24115000] = ICON('icons/24115000.png'),
              [24116000] = ICON('icons/24116000.png'),
              [24117000] = ICON('icons/24117000.png'),
              [24118000] = ICON('icons/24118000.png'),
              [24119000] = ICON('icons/24119000.png'),
              [24120000] = ICON('icons/24120000.png'),
              [24121000] = ICON('icons/24121000.png'),
              [24141000] = ICON('icons/24141000.png'),
              [24142000] = ICON('icons/24142000.png'),
              [24151000] = ICON('icons/24151000.png'),
              [24152000] = ICON('icons/24152000.png'),
              [24161000] = ICON('icons/24161000.png'),
              [24162000] = ICON('icons/24162000.png'),
              [24163000] = ICON('icons/24163000.png'),
              [34001000] = ICON('icons/34001000.png'),
              [34002000] = ICON('icons/34002000.png'),
              [35001000] = ICON('icons/35001000.png'),
            }
          }
        }
      }
    }
  }
}
