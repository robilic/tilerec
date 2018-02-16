<?php

$source_image = imagecreatefrompng("smb3map.png");

if (!$source_image) {
	die("Error reading source image");
}

echo "The image is " . imagesx($source_image) . " by " . imagesy($source_image) . "\n";

$source_image_bpp = 1;
$tile_h = $tile_w = 8;
$tile_len = $tile_w * $source_image_bpp;

// array to hold our tile hashes
$tile_list = array();

// each tile
for ($tile_x = 0; $tile_x < 16; $tile_x++) {
	for ($tile_y = 0; $tile_y < 14; $tile_y++) {
		$clipped_image = imagecreate($tile_w, $tile_h);
		imagepalettecopy($clipped_image, $source_image);
		$row_data = array();

		// each pixel of each tile
		for ($v = 0; $v < $tile_w; $v++) {
			for ($h = 0; $h < $tile_h; $h++) {
				$p = imagecolorat($source_image, ($tile_x * $tile_w) + $h, ($tile_y * $tile_h) + $v);
				imagesetpixel($clipped_image, $h, $v, $p);
				array_push($row_data, $p);
			}
		}
		$clipped_image_hash = md5(pack("C*", ...$row_data));
		// echo "\nLength: " . count($row_data) . " Hash: " . $clipped_image_hash . "\n";
		if (!in_array($clipped_image_hash, $tile_list)) {
			$tile_list[] =  $clipped_image_hash;
			imagepng($clipped_image, 'output-php/' . $clipped_image_hash . 	'.png');
		}		
	}
}

echo count($tile_list) . " unique tiles\n";
?>
