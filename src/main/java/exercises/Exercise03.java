/*
 * Excercise03.java
 * Copyright (C) 2020 Stephan Seitz <stephan.seitz@fau.de>
 *
 * Distributed under terms of the GPLv3 license.
 */

package exercises;

import mt.Image;
import mt.Vector2d;

public class Exercise03 {
	public static void main(String[] args) {
		(new ij.ImageJ()).exitWhenQuitting(true);

		Image image = lme.DisplayUtils.openImageFromInternet("https://mt2-erlangen.github.io/pacemaker.png", ".png");
		image.show();
		image.fft();

		// TODO: Create Cosine wave and compute fft
		// TODO: Generate checkboard patterns

	}
}
