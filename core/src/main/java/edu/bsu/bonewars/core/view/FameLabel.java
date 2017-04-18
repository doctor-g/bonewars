package edu.bsu.bonewars.core.view;

import static com.google.common.base.Preconditions.checkNotNull;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import react.AbstractValue;
import react.ValueView;
import tripleplay.ui.Icons;
import edu.bsu.bonewars.core.model.Fame;
import edu.bsu.bonewars.core.model.Player;

public class FameLabel extends AnimatedChangeLabel {
	private static final IDimension SIZE = new Dimension(56, 24);

	public static FameLabel createFor(Player player) {
		return new FameLabel(player);
	}

	private FameLabel(Player player) {
		super(SIZE, new FameAdapter(player.fame()), Palette.FAME_YELLOW);
		icon.update(Icons.image(GameImage.FAME_STAR.image));
	}

	private static final class FameAdapter extends AbstractValue<Integer> {

		private final ValueView<Fame> fame;

		private FameAdapter(ValueView<Fame> fame) {
			this.fame = checkNotNull(fame);
			fame.connect(new Listener<Fame>() {
				@Override
				public void onChange(Fame value, Fame oldValue) {
					emitChange(value.toInt(), oldValue.toInt());
				}
			});
		}

		@Override
		public Integer get() {
			return fame.get().toInt();
		}

	}
}
