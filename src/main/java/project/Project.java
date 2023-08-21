/*
 * Copyright (C) 2022 Bruno Riemenschneider <bruno.riemenschneider@fau.de>; Zhengguo Tan <zhengguo.tan@fau.de>; Jinho Kim <jinho.kim@fau.de>
 *
 * Distributed under terms of the GPLv3 license.
 */
package project;

import lme.DisplayUtils;
import mt.AverageFilter2d;
import mt.Signal;
import mt.Image;

import java.util.Arrays;

public class Project {
    public static ComplexImage kSpace2Image(ComplexImage kSpace) {
        // kspace -> Shift -> IFFT -> shift -> Image
        kSpace.fftShift();
        ComplexImage iFFT2D = ProjectHelpers.InverseFFT2D(kSpace);
        iFFT2D.fftShift();
        return iFFT2D;
    }
    public static ComplexImage Image2kSpace(ComplexImage image) {
        // Image -> FFT -> shift -> Kspace
        ComplexImage kSpace = ProjectHelpers.FFT2D(image);
        kSpace.fftShift();
        return kSpace;
    }
    public static void main(String[] args) {
        (new ij.ImageJ()).exitWhenQuitting(true);
        ComplexImage kSpace = ProjectHelpers.LoadKSpace("kdata.h5");
        /* Implement your code based on the project description */

        // Task 2.3 - Images and magnitudes of KSpace

        DisplayUtils.showImage(kSpace.getMagnitude(), "KSpace_Magnitude", kSpace.getWidth());
        DisplayUtils.showImage(kSpace.getLogMagnitude(), "KSpace_Magnitude - Log", kSpace.getWidth());
        DisplayUtils.showImage(kSpace.getPhase(), "KSpace_Phase", kSpace.getWidth());
        kSpace.getReal().show();
        kSpace.getImag().show();

        // Task 3.2 - Generating sine wave
        int length = 256;
        ComplexSignal sineWave = new ComplexSignal(length, "sine wave");
        sineWave.generateSine(5);
        DisplayUtils.showArray(sineWave.getReal(), "Sine Wave - real", 0, 1);
        DisplayUtils.showArray(sineWave.getImag(), "Sine Wave - imaginary", 0, 1);
        DisplayUtils.showArray(sineWave.getMagnitude(), "Sine Wave - magnitude", 0, 1);

        // Task 3.2 - FFT and Shift to Sine wave (1D)
        ComplexSignal fftSine = ProjectHelpers.FFT1D(sineWave);
        DisplayUtils.showArray(fftSine.getMagnitude(), "FFT Sine wave", 0, 1);
        // shifting the FFT signal
        fftSine.fftShift1d();
        DisplayUtils.showArray(fftSine.getMagnitude(), "FFT sine shifted", 0, 1);
//        fftSine.fftShift1d();
//        DisplayUtils.showArray(fftSine.getMagnitude(), "FFT sine shifted Again", 0, 1);

        // Task 3.3 - Expanding to 2D FFT
        kSpace = ProjectHelpers.LoadKSpace("kdata.h5");
        ComplexImage fft2D = ProjectHelpers.FFT2D(kSpace);
        DisplayUtils.showImage(fft2D.getLogMagnitude(), "FFT2D Magnitude - Log", kSpace.getWidth());
        fft2D.fftShift();
        DisplayUtils.showImage(fft2D.getLogMagnitude(), "FFT2D Shifted Magnitude - Log", kSpace.getWidth());

//        fft2D.fftShift();
//        DisplayUtils.showImage(fft2D.getLogMagnitude(), "FFT2D Shifted Again Magnitude - Log", kSpace.getWidth());

        // Task 3.4 - Reconstructing MR image and KSpace

        kSpace = ProjectHelpers.LoadKSpace("kdata.h5");
        DisplayUtils.showImage(kSpace.getLogMagnitude(), "KSpace Log Mag- Orig", kSpace.getWidth());
        ComplexImage reImage = kSpace2Image(kSpace);
        DisplayUtils.showImage(reImage.getMagnitude(), "Reconstructed Image", reImage.getWidth());
        DisplayUtils.showImage(reImage.getPhase(), "Reconstructed Image Phase", reImage.getWidth());
        DisplayUtils.showImage(reImage.getReal().buffer(), "Reconstructed Image Real", reImage.getWidth());
        DisplayUtils.showImage(reImage.getImag().buffer(), "Reconstructed Image Imag", reImage.getWidth());
        ComplexImage kSpaceReconstructed = Image2kSpace(reImage);
        DisplayUtils.showImage(kSpaceReconstructed.getLogMagnitude(), "KSpace Log Mag- Reconstructed", kSpace.getWidth());
        DisplayUtils.showImage(kSpaceReconstructed.getPhase(), "KSpace phase- Reconstructed", kSpace.getWidth());

        // filters
        // Task 4.1.1 - Sinc Filter
        kSpace = ProjectHelpers.LoadKSpace("kdata.h5");
        ComplexImage fImage = kSpace2Image(kSpace);
        SincFilter2d realFilter = new SincFilter2d(31, 4.0f);
        LinearComplexImageFilter complexFilter = new LinearComplexImageFilter(realFilter);
        ComplexImage filteredImage = complexFilter.apply(fImage);
        DisplayUtils.showImage(filteredImage.getMagnitude(), "sinc filter", filteredImage.getWidth());
        DisplayUtils.showImage(Image2kSpace(filteredImage).getLogMagnitude(), "sinc kspace", filteredImage.getWidth());

        // Task 4.1.2 - Box Filter

        kSpace = ProjectHelpers.LoadKSpace("kdata.h5");
//        DisplayUtils.showImage(kSpace.getLogMagnitude(), "Before Box filter", kSpace.getWidth());
        kSpace.setOuterToZero(96, 0);
        kSpace.setOuterToZero(96, 1);
        DisplayUtils.showImage(kSpace.getLogMagnitude(), "After Box filter", kSpace.getWidth());
        DisplayUtils.showImage(kSpace2Image(kSpace).getMagnitude(), "reconstructed from box kspace", kSpace.getWidth());

        // Task 4.2.1 - Cropping KSpace

        kSpace = ProjectHelpers.LoadKSpace("kdata.h5");
        int cropWidth = kSpace.getWidth()/4;
        int cropHeight = kSpace.getHeight()/4;
        ComplexImage croppedKSpace = new ComplexImage(cropWidth, cropHeight, "croppedKSpace", kSpace.getReal().buffer(), kSpace.getImag().buffer(), kSpace.getWidth(), kSpace.getHeight());
        DisplayUtils.showImage(croppedKSpace.getLogMagnitude(), "croppedKSpace", croppedKSpace.getWidth());
        DisplayUtils.showImage(kSpace2Image(croppedKSpace).getMagnitude(), "reconstructedCroppedImage", croppedKSpace.getWidth());


        // Task 4.2.2 - MaxPooling2d
        kSpace = ProjectHelpers.LoadKSpace("kdata.h5");
        ComplexImage mrImage = kSpace2Image(kSpace);
        float[] mag = mrImage.getMagnitude();
        Image mrMagImage = new Image(mrImage.getWidth(), mrImage.getHeight(), "magnitude of mrImage");
        mrMagImage.setBuffer(mag);
//        DisplayUtils.showImage(mrMagImage.buffer(), "After max pooling", mrMagImage.width());
        MaxPooling2d mp = new MaxPooling2d(4, 4, 4, 4);
        Image mrMagImage_MP = mp.apply(mrMagImage);
        DisplayUtils.showImage(mrMagImage_MP.buffer(), "After max pooling", mrMagImage_MP.width());

    }
}
