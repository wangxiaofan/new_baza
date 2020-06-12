package baza.dialog.simpledialog;

/**
 * Created by lee on 2014/7/30.
 */
public enum Effectstype {

	// Fadein(FadeIn.class),
	Slideleft(SlideLeft.class),
	// Slidetop(SlideTop.class),
	SlideBottom(SlideBottom.class), 
	Slideright(SlideRight.class), 
	Fall(Fall.class), 
	Newspager(NewsPaper.class),
	// Fliph(FlipH.class),
	// Flipv(FlipV.class),
	// RotateBottom(RotateBottom.class),
	// RotateLeft(RotateLeft.class),
	Slit(Slit.class),
	// Sidefill(SideFall.class),
	Shake(Shake.class);
	private Class<?> effectsClazz;

	Effectstype(Class<?> mclass) {
		effectsClazz = mclass;
	}

	public BaseEffects getAnimator() {
		try {
			return (BaseEffects) effectsClazz.newInstance();
		} catch (Exception e) {
			throw new Error("Can not init animatorClazz instance");
		}
	}
}
