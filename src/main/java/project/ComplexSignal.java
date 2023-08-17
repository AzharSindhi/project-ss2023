package project;

import lme.DisplayUtils;
import mt.Signal;
import mt.SineWave;

import java.util.Objects;

public class ComplexSignal {
    protected mt.Signal real;    //Image object to store real part
    protected mt.Signal imag;    //Image object to store imaginary part
    protected String name;      //Name of the image
    public ComplexSignal(int length, String name)
    {
        real = new Signal(length, name+"_real");
        imag = new Signal(length, name + "_imag");
        this.name = name;
    }
    public ComplexSignal(float[] signalReal, float[] signalImag, String name)
    {
        this.name = name;
        real = new Signal(signalReal, name+"_real");
        imag = new Signal(signalImag, name+"_imag");
    }
    private Signal calculateMagnitude() {
        int length = this.getSize();
        Signal magSignal = new Signal(length, this.name + "_magnitude");
        for (int i = 0; i < length; i++) {
            float real = this.real.atIndex(i);
            float imag = this.imag.atIndex(i);
            float mag = (float) Math.sqrt(real * real + imag * imag);
            magSignal.setAtIndex(i, mag);

        }
        return magSignal;
    }
    public float[] getMagnitude() {
        return calculateMagnitude().buffer();
    }
    public void generateSine(int numWaves){
        // taken from exercise 1
        real = new SineWave(numWaves, this.getSize())
                .plus(new mt.SineWave(2 * numWaves, this.getSize()).times(-1.0f/2.0f))
                .plus(new mt.SineWave(3 * numWaves, this.getSize()).times(+1.0f/3.0f))
                .plus(new mt.SineWave(4 * numWaves, this.getSize()).times(-1.0f/4.0f))
                .plus(new mt.SineWave(5 * numWaves, this.getSize()).times(+1.0f/5.0f));
        imag = real.times(0);
        real.setName(this.name + "_real");
        imag.setName(this.name + "_imag");
    }
    private Signal swap(Signal input) {
        int right = input.size() / 2;
        for (int left = 0; left < input.size()/2; left++) {
            float leftValue = input.atIndex(left);
            float rightValue = input.atIndex(right);
            input.setAtIndex(left, rightValue);
            input.setAtIndex(right, leftValue);
            right++;
        }
        return input;
    }

    public void fftShift1d() {
        real = this.swap(real);
        imag = this.swap(imag);
    }

    public float[] getReal(){
        // get the buffer of the real
        return real.buffer();
    }

    public float[] getImag(){
        // get the buffer of the imag
        return imag.buffer();
    }
    public String getName(){
        return name;
    }
    public int getSize(){
        return real.size();
    }
}