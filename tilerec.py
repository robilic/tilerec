
from PIL import Image
import hashlib

source_image = Image.open("smb3map.png")

print("The image is ", source_image.width, " by ", source_image.height, "Mode:" , source_image.mode)

source_image_data = source_image.tobytes()
source_image_palette = source_image.getpalette()

#print(source_image_data)
print("Image size in bytes:", len(source_image_data))

source_image_bpp = 1 # bytes per pixel
tile_h = tile_w = 8	
tile_len = tile_w * source_image_bpp # how many pixels to scan on each line

tile_dict = {} # store our hashes of tiles

#256x225
for x in range(0, 16):
	for y in range(0,14):
		clipped_image_data = b''
		for h in range(0, tile_h):
			offset = (x*tile_w) + (((y*tile_h)+h)*source_image.width)
			print("X:", (x*tile_w), "Y:", (((y*tile_h)+h)*source_image.width))	
			print("Offset:", offset, "Len:", tile_len, "H:", h)
			buf = source_image_data[offset:offset+tile_len]
			print("Buf:", buf)
			clipped_image_data = clipped_image_data + buf

		print("clipped_image_data tile size = " + str(len(clipped_image_data)))

		clipped_image = Image.frombytes('P', [tile_w, tile_h], clipped_image_data);
		clipped_image_hash = hashlib.md5(clipped_image_data).hexdigest()

		if clipped_image_hash not in tile_dict:
			tile_dict[clipped_image_hash] = str(x + (y * tile_w))
			#copy the palette from the original image to the new image
			clipped_image.putpalette(source_image_palette)

			clipped_image.save('output-python/' + clipped_image_hash + '.png') # filename = clipped_image_hash-x-y.png

print(str(len(tile_dict)), "unique tiles")


