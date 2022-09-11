#include <iostream>
#include <set>

#include <stdio.h>
#include <stdlib.h>

#include "openssl/md5.h"
#include "lodepng.h"

using namespace std;
      
int main() {
  unsigned error;
  unsigned char* image = 0;
  unsigned pngWidth, pngHeight;

  unsigned char* tileImage;
  unsigned tileWidth, tileHeight;
  unsigned tileBpp;
  int tileSize;

  unsigned char hash[32];
  set<string> tileHashes;

  tileWidth = tileHeight = 8;
  tileBpp = 4;
  tileSize = tileWidth * tileHeight * tileBpp;
  tileImage = (unsigned char*)malloc(tileSize);

  error = lodepng_decode32_file(&image, &pngWidth, &pngHeight, "smb3map.png");
  if(error) printf("error %u: %s\n", error, lodepng_error_text(error));

  cout << "Image dimensions: " << pngWidth << "x" << pngHeight << endl;
  // image[] is our image data in rgba format

  // go through each tile in image
  for (int iy = 0; iy < pngHeight; iy += tileHeight)
  {
    for (int ix = 0; ix < pngWidth; ix += tileWidth)
    {
      // each pixel in tile
      for (int ty = 0; ty < tileHeight; ty++)
      {
      for (int tx = 0; tx < tileWidth; tx++)
        {
          tileImage[((tx + (ty * tileWidth)) * tileBpp)+0] = image[(((ix + (iy * pngWidth)) * tileBpp) + ((tx + (ty * pngWidth)) * tileBpp))+0];
          tileImage[((tx + (ty * tileWidth)) * tileBpp)+1] = image[(((ix + (iy * pngWidth)) * tileBpp) + ((tx + (ty * pngWidth)) * tileBpp))+1];
          tileImage[((tx + (ty * tileWidth)) * tileBpp)+2] = image[(((ix + (iy * pngWidth)) * tileBpp) + ((tx + (ty * pngWidth)) * tileBpp))+2];
          tileImage[((tx + (ty * tileWidth)) * tileBpp)+3] = image[(((ix + (iy * pngWidth)) * tileBpp) + ((tx + (ty * pngWidth)) * tileBpp))+3];
        }
      }

      MD5((const unsigned char*)tileImage, tileSize, hash);
      // add current hash to list of hashes (if it doesn't already exist)
      // also write it out to a png file
      if ( tileHashes.insert(string(hash, hash+16)).second )
      {
        char ofilename[] = "                                .png";
        for (int i = 0, j = 0; i < 16; i++, j+=2)
        {
          sprintf(ofilename+j, "%02x", hash[i]);
        }
        sprintf(ofilename+32, ".png");
        printf("Writing to '%s'\n", ofilename);
        lodepng_encode32_file(ofilename, tileImage, tileWidth, tileHeight);
      }
    }
  }

  free(image);
  free(tileImage);

  return 0;
}
