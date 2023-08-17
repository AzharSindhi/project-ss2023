package project;

import mt.Image;

public class ComplexImage {
    protected mt.Image real;    //Image object to store real part
    protected mt.Image imag;    //Image object to store imaginary part
    protected String name;      //Name of the image
    protected int width;
    protected int height;
    public ComplexImage(int width, int height, String name){
        this.width = width;
        this.height = height;
        this.name = name;
        // have to do this, otherwise there is error in ProjectHelpers loader
        this.real = new Image(width, height, name+"_real");
        this.imag = new Image(width, height, name + "_imag");
    }
    public ComplexImage(int width, int height, String name, float[] bufferReal, float[] bufferImag){
        this.width = width;
        this.height = height;
        this.name = name;
        real = new Image(width, height, name + "_real", bufferReal);
        imag = new Image(width, height, name + "_imag", bufferImag);
    }
    public ComplexImage(int width, int height, String name, float[] bufferReal, float[] bufferImag, int inputWidth, int inputHeight){
        this.width = width;
        this.height = height;
        real = new Image(width, height, name + "_real");
        imag = new Image(width, height, name + "_imag");
        real.setBufferFromCenterArea(width, height, bufferReal, inputWidth, inputHeight);
        imag.setBufferFromCenterArea(width, height, bufferImag, inputWidth, inputHeight);
    }

    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }
    public String getName(){
        return name;
    }
    private Image calculateMagnitude(boolean logFlag){
        Image magnitudeImg = new Image(width, height, "magnitudeImg");
        for (int h=0; h<height; h++) {
            for (int w=0; w<width; w++) {
                float real = this.real.atIndex(w, h);
                float imag = this.imag.atIndex(w, h);
                float mag = (float) Math.sqrt(real * real + imag * imag);
                if (logFlag) {mag = (float) Math.log(mag);}                
                magnitudeImg.setAtIndex(w, h, mag); 
            }
        }
        return magnitudeImg;
    }
    private Image calculatePhase(){
        Image phaseImg = new Image(width, height, "PhaseImg");
        for (int h=0; h<height; h++) {
            for (int w=0; w<width; w++) {
                double real = (double) this.real.atIndex(w, h);
                double imag = (double) this.real.atIndex(w, h);
                float value = (float) Math.atan2(real, imag);
                phaseImg.setAtIndex(w, h, value); 
            }
        }
        return phaseImg;
    }
    private Image swap (Image input, int startCol , int swapColOffset) {
        int w = input.width();
        int h = input.height();
        int rowOffset = h/2; // always same in our case
        int endCol = startCol + w/2;
        for (int i = 0; i<h/2; i++) {
            for (int j=startCol; j<endCol; j++){
                float matValue = input.atIndex(i, j);
                float swapValue = input.atIndex(i + rowOffset, j+swapColOffset);
                input.setAtIndex(i, j, swapValue);
                input.setAtIndex(i + rowOffset, j+swapColOffset, matValue);
            }
        }
        return input;
    }
    private Image swapQuadrants(Image input){
        int w = input.width();
        int h = input.height();
        swap(input, 0, w / 2);
        swap(input, w / 2, -w / 2);
        return input;
    }

    public void fftShift(){
        real = swapQuadrants(real);
        imag = swapQuadrants(imag);
    }
    public Image applyBox(Image input, int startRow, int startCol, int inputWidth, int inputHeight) {
        Image output = new Image(width, height, name + "Box filter");

        for (int x=startRow; x<inputHeight; x++) {
            for (int y=startCol; y<inputWidth; y++) {
                output.setAtIndex(x, y, input.atIndex(x, y));
            }
        }
        return output;
    }
    public void setOuterToZero(int lines, int axis) {
        int startRow = 0;
        int startCol = 0;
        int inputWidth = width;
        int inputHeight = height;
        if (axis == 1) {
            inputHeight -= lines;
            startRow = lines;
        }
        else {
            inputWidth -= lines;
            startCol = lines;
        }

        real = applyBox(real, startRow, startCol, inputWidth, inputHeight);
        imag = applyBox(imag, startRow, startCol, inputWidth, inputHeight);
    }

    public float[] getMagnitude(){
        return calculateMagnitude(false).buffer();
    }
    public float[] getLogMagnitude(){
        return calculateMagnitude(true).buffer();
    }
    public float[] getPhase(){
        return calculatePhase().buffer();
    }
    public Image getReal() {return real;}
    public Image getImag() {return imag;}
}