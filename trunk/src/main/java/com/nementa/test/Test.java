package com.nementa.test;

import java.util.Random;

/** Generate random integers in a certain range. */
public final class Test {

	public static final void main(String... aArgs) {

		int start = 12;
		int end = 15;
		int rnd = start + new Random().nextInt(end - start);
		System.out.println("mijn rnd=" + rnd);
	}
}
