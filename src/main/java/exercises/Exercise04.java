package exercises;

import mt.*;

public class Exercise04 {
    public static void main(String[] args) {
        (new ij.ImageJ()).exitWhenQuitting(true);

        Image image = lme.DisplayUtils.openImageFromInternet("https://mt2-erlangen.github.io/pacemaker.png", ".png");
        image.show();
        Image image_hand = lme.DisplayUtils.openImage("data/hand.png");
        image_hand.show();
        // TODO: Initialize, apply and show all filters
    }
}
