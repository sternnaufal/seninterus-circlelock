from PIL import Image, ImageDraw
import os

source = Image.open('D:/ProjectGame/Android Studio Game/seninterus-circlelock/ic_launcher-playstore.png')

sizes = {
    'mipmap-mdpi': 48,
    'mipmap-hdpi': 72,
    'mipmap-xhdpi': 96,
    'mipmap-xxhdpi': 144,
    'mipmap-xxxhdpi': 192,
}

# Adaptive icon sizes: 108dp canvas at each density
# mdpi=108, hdpi=162, xhdpi=216, xxhdpi=324, xxxhdpi=432
adaptive_sizes = {
    'drawable-mdpi': 108,
    'drawable-hdpi': 162,
    'drawable-xhdpi': 216,
    'drawable-xxhdpi': 324,
    'drawable-xxxhdpi': 432,
}

base_path = 'D:/ProjectGame/Android Studio Game/seninterus-circlelock/app/src/main/res'

# Generate mipmap raster icons (ic_launcher.webp + ic_launcher_round.webp)
for folder, size in sizes.items():
    resized = source.resize((size, size), Image.LANCZOS)
    path = os.path.join(base_path, folder, 'ic_launcher.webp')
    resized.save(path, 'WEBP', quality=90)
    print(f'Saved {path}')
    
    mask = Image.new('L', (size, size), 0)
    mask_draw = ImageDraw.Draw(mask)
    mask_draw.ellipse([0, 0, size-1, size-1], fill=255)
    
    round_icon = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    round_icon.paste(resized, (0, 0), mask)
    
    path_round = os.path.join(base_path, folder, 'ic_launcher_round.webp')
    round_icon.save(path_round, 'WEBP', quality=90)
    print(f'Saved {path_round}')

# Generate adaptive icon raster layers for anydpi-v26
# Adaptive icon: 108dp canvas, safe zone = inner 66.67% (72dp diameter)
# Icon content must fit within safe zone, centered in canvas
for folder, canvas_size in adaptive_sizes.items():
    safe_zone = int(canvas_size * 72 / 108)  # 66.67% of canvas
    
    # Create foreground: scale playstore icon into safe zone, center on canvas
    fg = Image.new('RGBA', (canvas_size, canvas_size), (0, 0, 0, 0))
    icon_in_safe = source.resize((safe_zone, safe_zone), Image.LANCZOS).convert('RGBA')
    offset = (canvas_size - safe_zone) // 2
    fg.paste(icon_in_safe, (offset, offset), icon_in_safe)
    
    fg_path = os.path.join(base_path, folder, 'ic_launcher_foreground.webp')
    fg.save(fg_path, 'WEBP', quality=90)
    print(f'Saved {fg_path}')
    
    # Create background: solid dark color matching playstore icon bg (#0A0A0A)
    bg = Image.new('RGBA', (canvas_size, canvas_size), (10, 10, 10, 255))
    bg_path = os.path.join(base_path, folder, 'ic_launcher_background.webp')
    bg.save(bg_path, 'WEBP', quality=90)
    print(f'Saved {bg_path}')

print('\nAll icons updated!')
