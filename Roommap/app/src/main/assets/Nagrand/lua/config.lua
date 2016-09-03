local module = GetModule('Lua')

local LoadStyleProxy;

local function LoadColorPointStyle(config, _3d)
	local style = _3d and CreateStyle('color_point_3d') or CreateStyle('color_point_2d')
	local size = config.size
	local color = config.color
	local shape = config.shape
	local rotate = config.rotate
  local enable_alpha = config.enable_alpha

	if size then style.size = size end
	if color then style.color = color end
	if shape then config.shape = shape end
	if rotate then config.rotate = rotate end
  if enable_alpha ~= nil then style.enable_alpha = enable_alpha end

  return style
end

local function LoadIconStyle(config, _3d)
	local style = _3d and CreateStyle('icon_3d') or CreateStyle('icon_2d')
	if not _3d then
		local width = config.width
		local height = config.height
		local use_texture_origin_size = config.use_texture_origin_size
		local icon = config.icon
        local anchor_x = config.anchor_x
        local anchor_y = config.anchor_y

		if width then style.width = width end
		if height then style.height = height end
		if use_texture_origin_size ~= nil then style.use_texture_origin_size = use_texture_origin_size end
		if icon then style.icon = icon end
        if anchor_x then style.anchor_x = anchor_x end
        if anchor_y then style.anchor_y = anchor_y end
	else
		local top_edge_width = config.top_edge_width
		local bottom_edge_width = config.bottom_edge_width
		local height = config.height
		local top_color = config.top_color
		local bottom_color = config.bottom_color
		local icon = config.icon

		if top_edge_width then style.top_edge_width = top_edge_width end
		if bottom_edge_width then style.bottom_edge_width = bottom_edge_width end
		if height then style.height = height end
		if top_color then style.top_color = top_color end
		if bottom_color then icon.bottom_color = bottom_color end
		if icon then style.icon = icon end
	end

	return style
end

local function LoadAnnotationStyle(config, _3d)
  local style = _3d and CreateStyle('annotation_3d') or CreateStyle('annotation_2d')
  if not _3d then
    local color = config.color
    local field = config.field
    local size = config.size
    local outline_color = config.outline_color
    local outline_width = config.outline_width
    local anchor_style = config.anchor_style
    
    local anchor_x = config.anchor_x
    local anchor_y = config.anchor_y

    if color then style.color = color end
    if field then style.field = field end
    if size then style.size = size end
    if outline_width then style.outline_width = outline_width end
    if outline_color then style.outline_color = outline_color end
    if anchor_x then style.anchor_x = anchor_x end
    if anchor_y then style.anchor_y = anchor_y end
    if anchor_style then style.anchor_style = LoadStyleProxy(anchor_style, false) end
  else
  end

  return style
end

local function LoadColorFaceStyle(config, _3d)
	local style = CreateStyle('color_face')

	local color = config.color
	local enable_alpha = config.enable_alpha

	if color then style.color = color end
	if enable_alpha ~= nil then style.enable_alpha = enable_alpha end
	return style
end

local function LoadTextureFaceStyle(config, _3d)
	local style = CreateStyle('texture_face')

	local texture = config.texture
	local automatic_scale = config.automatic_scale
	local scale_x = config.scale_x
	local scale_y = config.scale_y
  local enable_alpha = config.enable_alpha

  if texture then style.texture = texture end
	if automatic_scale then style.automatic_scale = automatic_scale end
  if enable_alpha ~= nil then style.enable_alpha = enable_alpha end
	if scale_x then style.scale_x = scale_x end
	if scale_y then style.scale_y = scale_y end

	return style
end

local function LoadFaceStyle(config, _3d)
	if config.color then
		return LoadColorFaceStyle(config, _3d)
	elseif config.texture then
		return LoadTextureFaceStyle(config, _3d)
	end
end

local function LoadSegmentStyle(config, _3d)
	local style = _3d and CreateStyle('segment_3d') or CreateStyle('segment_2d')
	local color = config.color
	local width = config.width
  	local enable_alpha = config.enable_alpha

  	if color then style.color = color end
	if width then style.width = width end
  	if enable_alpha ~= nil then style.enable_alpha = enable_alpha end

  	if _3d then
		local left = config.left_side and LoadFaceStyle(config.left_side)
		local right = config.right_side and LoadFaceStyle(config.right_side)
		local top = config.top_side and LoadFaceStyle(config.top_side)
		local height = config.height

		if left then style.left_side = left end
		if right then style.right_side = right end
		if top then style.top_side = top end
		if height then style.height = height end
  	else
    	local line_style = config.line_style
        local has_arrow = config.has_arrow

    	if line_style then style.line_style = line_style end
        if has_arrow ~= nil then style.has_arrow = has_arrow end
  	end

	return style
end

local function LoadLineStringStyle(config, _3d)
	local style = CreateStyle('linestring')
	local default = LoadSegmentStyle(config, _3d)
	local has_end = config.has_end
	local has_start = config.has_start

	if has_end ~= nil then style.has_end = has_end end
	if has_start ~= nil then style.has_start = has_start end
	if default then style.default = default end
	return style
end

local function LoadPolygonStyle(config, _3d)
	local style = CreateStyle('polygon')
	local face = config.face and LoadFaceStyle(config.face, _3d)
	local outline = config.outline and LoadLineStringStyle(config.outline, _3d)
	local height = config.height
	local enable_hole_outline = config.enable_hole_outline
	local face_on_bottom = config.face_on_bottom

	if face then style.face = face end
	if outline then style.outline = outline end
	if height then style.height = height end
	if enable_hole_outline ~= nil then style.enable_hole_outline = enable_hole_outline end
	if face_on_bottom ~= nil then style.face_on_bottom = face_on_bottom end

	return style
end

local function LoadStyle(config, _3d)
	local style_type = config.style

	if style_type == 'polygon' then
		return LoadPolygonStyle(config, _3d)
	elseif style_type == 'linestring' then
		return LoadLineStringStyle(config, _3d)
	elseif style_type =='color_point' then
		return LoadColorPointStyle(config, _3d)
	elseif style_type =='annotation' then
		return LoadAnnotationStyle(config, _3d)
	elseif style_type =='icon' then
		return LoadIconStyle(config, _3d)
  end
end

LoadStyleProxy = LoadStyle

local function LoadStyleConfig(config)
  local style
  if config['2d'] and config['3d'] then
		style = CreateStyle('switchable')
		style['2d'] = LoadStyle(config['2d'])
		style['3d'] = LoadStyle(config['3d'], true)
	else
		style = LoadStyle(config['2d'] or config)
	end
	return style
end

local function LoadSimpleRendererConfig(renderer, config)
	local style
	if config['2d'] and config['3d'] then
		style = CreateStyle('switchable')
		style['2d'] = LoadStyle(config['2d'])
		style['3d'] = LoadStyle(config['3d'], true)
	else
		style = LoadStyle(config['2d'] or config)
	end

	renderer.style = style
end

local function LoadUniqueValueRendererConfdig(renderer, config)
  if config.key then
    if type(config.key) == "table" then
      for k, v in pairs(config.key) do
        renderer.key = v
      end
    elseif type(config.key) == "string" then
          renderer.key = config.key
    end
  else
    config.key = 'id'
  end

  if config.default then
  	renderer.default = LoadStyleConfig(config.default);
  end

  if config.styles then
  	local styles = renderer.styles
    for k, v in pairs(config.styles) do
      styles[k] = LoadStyleConfig(v)
    end
  end
end

local function LoadViewConfig(view, config)
	local layers = config.layers
	RegisterEvent(view, 'LAYER_ADDING', function(view, layer)
		local config = layers[layer.name]
		if config then
      local height_offset = config.height_offset
      local font_path = config.font_path
      local collision_detection = config.collision_detection
      local allow_merge = config.allow_merge
      if height_offset then layer.height_offset = height_offset end
      if font_path then layer.font_path = font_path end
      if collision_detection ~= nil then layer.collision_detection = collision_detection end
      if allow_merge ~= nil then layer.allow_merge = allow_merge end

      local renderer_conf = config.renderer
			if renderer_conf then
				local type = renderer_conf.type
				if not type then
					error('Missing renderer type for layer ' .. layer.name .. '.')
				end

				local renderer = CreateRenderer(type)
				layer.renderer = renderer


				if type == 'simple' then
					LoadSimpleRendererConfig(renderer, renderer_conf)
				elseif type == 'unique' then
					LoadUniqueValueRendererConfdig(renderer, renderer_conf)
				end
			end
		end
	end)
end

local function LoadConfig(config)
	local view_module = GetModule('MapView')
	if type(config) == 'function' then
		config = config()
	end
	RegisterEvent(view_module, 'LOADING_MAPVIEW', function(module, view)
		print('MapView ' .. view.name .. ' loaded.')
		local views = config.views or {}
		local conf = views[view.name] or {}
		LoadViewConfig(view, conf)
	end)
end

if module then
	RegisterEvent(module, 'LOADED', function()
		if CONFIG then LoadConfig(CONFIG) end
	end)
end
