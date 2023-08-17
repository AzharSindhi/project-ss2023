package project;

import mt.Image;
import mt.LinearImageFilter;

public class LinearComplexImageFilter {
    LinearImageFilter filter;
    public LinearComplexImageFilter(LinearImageFilter filter) {
        this.filter = filter;
    }
    public ComplexImage apply(ComplexImage input) {
        float[] real = this.filter.apply(input.getReal()).buffer();
        float[] imag = this.filter.apply(input.getImag()).buffer();
        ComplexImage output = new ComplexImage(input.getWidth(), input.getHeight(), input.getName() + filter.name(), real, imag);
        return output;
    }
}
