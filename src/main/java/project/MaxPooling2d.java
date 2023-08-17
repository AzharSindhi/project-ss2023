package project;

import mt.Image;

public class MaxPooling2d {

    protected int block_width = 0;
    protected int block_height = 0;
    protected int stride_width = 0;
    protected int stride_height = 0;
    protected String name = "MaxPooling2d";

    public MaxPooling2d(int block_width, int block_height, int stride_width, int stride_height) {

        this.block_width = block_width;
        this.block_height = block_height;
        this.stride_width = stride_width;
        this.stride_height = stride_height;
    }

    public Image apply(Image input) {

        /* your code here */
        int new_width = (input.width() - block_width)/stride_width + 1;
        int new_height = (input.height() - block_height) / stride_height + 1;
        Image output = new Image(new_width, new_height, "max pooling");
        for (int i=0; i<new_width; i++) {
            for (int j=0; j<new_height; j++){
                float maxVal = Float.NEGATIVE_INFINITY;
                for (int k=i*stride_width; k< i*stride_width + block_width; k++){
                    for (int l=j*stride_height; l < j*stride_height + block_height; l++){
                        if (input.atIndex(k, l) > maxVal) maxVal=input.atIndex(k, l);

                    }
                }
                output.setAtIndex(i, j, maxVal);
            }
        }
        return output;

    }
}