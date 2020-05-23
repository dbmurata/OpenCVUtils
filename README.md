# OpenCVUtils

A wrapper library to make OpenCV matrices act like Java BufferedImages.

## Installation

Coming soon...

## Usage

```BufferedImage image = new OpenCVImage(200, 200, BufferedImage.TYPE_INT_RGB); 
Graphics2D g = image.createGraphics(); 
g.setColor(new Color(255, 255, 255)); 
g.fillRect(0, 0, 200, 200); 
g.setColor(new Color(0, 0, 0)); 
g.drawRect(10, 10, 180, 180); 
```
