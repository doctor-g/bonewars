package edu.bsu.bonewars.core.view;

import static com.google.common.base.Preconditions.checkNotNull;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import react.AbstractValue;
import react.ValueView;
import edu.bsu.bonewars.core.model.Funds;
import edu.bsu.bonewars.core.model.Player;

public class FundsLabel extends AnimatedChangeLabel {

	private static final IDimension SIZE = new Dimension(56, 24);

	public static FundsLabel createFor(Player player) {
		return new FundsLabel(player);
	}

	private FundsLabel(final Player player) {
		super(SIZE, new FundsAdapter(player.funds()), Palette.FUNDS_GREEN);
	}

	private static final class FundsAdapter extends AbstractValue<Integer> {

		private final ValueView<Funds> funds;

		public FundsAdapter(ValueView<Funds> funds) {
			super();
			this.funds = checkNotNull(funds);
			funds.connect(new Listener<Funds>() {
				@Override
				public void onChange(Funds value, Funds oldValue) {
					emitChange(value.asInt(), oldValue.asInt());
				}
			});
		}

		@Override
		public Integer get() {
			return funds.get().asInt();
		}

	}

	@Override
	protected void updateText(int i) {
		text.update("$" + i);
	}

}
