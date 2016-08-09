#include "VideoData.h"
#include <string.h>

VideoData::VideoData(unsigned int size, unsigned char *buffer)
{
	length = size;
	memcpy(data, buffer, size);
}
