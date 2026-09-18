from PIL import Image, ImageDraw
import math

size = 512
center = size // 2

# Colors
primary_gold = (255, 215, 0)
secondary_gold = (197, 160, 33)
background = (10, 10, 10)

# Create image
img = Image.new('RGB', (size, size), background)
draw = ImageDraw.Draw(img)

def draw_ring_with_gap(radius, thickness, color, gap_degrees=15):
    bbox = [center - radius, center - radius, center + radius, center + radius]
    start_angle = gap_degrees // 2
    end_angle = 360 - gap_degrees // 2
    draw.arc(bbox, start_angle, end_angle, fill=color, width=thickness)

# Outer ring - gap at 3 o'clock
draw_ring_with_gap(180, 28, primary_gold, 18)

# Middle ring - gap at 3 o'clock  
draw_ring_with_gap(140, 22, secondary_gold, 18)

# Inner ring - gap at 3 o'clock
draw_ring_with_gap(100, 18, primary_gold, 18)

# Lock body - centered vertically
lock_center_y = center - 5
lock_x1 = center - 50
lock_y1 = lock_center_y - 25
lock_x2 = center + 50
lock_y2 = lock_center_y + 45
draw.rounded_rectangle([lock_x1, lock_y1, lock_x2, lock_y2], radius=12, fill=primary_gold)

# Lock shackle (arc) - centered with body
shackle_bbox = [center - 35, lock_center_y - 60, center + 35, lock_center_y + 10]
draw.arc(shackle_bbox, 180, 0, fill=primary_gold, width=18)

# Keyhole circle
keyhole_r = 10
keyhole_y = lock_center_y + 5
draw.ellipse([center - keyhole_r, keyhole_y - keyhole_r, 
              center + keyhole_r, keyhole_y + keyhole_r], fill=background)

# Keyhole rectangle
draw.rectangle([center - 5, keyhole_y + 5, center + 5, keyhole_y + 30], fill=background)

# Add subtle glow effect
glow = Image.new('RGBA', (size, size), (0, 0, 0, 0))
glow_draw = ImageDraw.Draw(glow)
for r in range(200, 0, -4):
    alpha = int(30 * (1 - r / 200))
    glow_draw.ellipse([center - r, center - r, center + r, center + r], 
                      fill=(255, 215, 0, alpha))

img = Image.alpha_composite(img.convert('RGBA'), glow).convert('RGB')

# Save
img.save('D:/ProjectGame/Android Studio Game/seninterus-circlelock/ic_launcher-playstore.png', 'PNG')
print("Icon saved successfully!")
