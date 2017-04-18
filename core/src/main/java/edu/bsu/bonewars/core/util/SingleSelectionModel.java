package edu.bsu.bonewars.core.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import react.UnitSignal;

public final class SingleSelectionModel<T> {

	public static <T> SingleSelectionModel<T> create() {
		return new SingleSelectionModel<T>();
	}

	private final UnitSignal change = new UnitSignal();
	private T selection;
	
	private interface State<T> {
		public void select(T item);

		public boolean hasSelection();

		public T deselect();

		public T selection();

		public boolean isEnabled();

		public void disable();

		public void enable();
	}

	private final State<T> enabledState = new State<T>() {
		
		@Override
		public void select(T item) {
			selection = checkNotNull(item);
			change.emit();
		}

		@Override
		public boolean hasSelection() {
			return selection != null;
		}

		@Override
		public T deselect() {
			checkState(hasSelection());
			T theDeselectedObject = selection;
			selection = null;
			change.emit();
			return theDeselectedObject;
		}

		@Override
		public T selection() {
			checkState(hasSelection());
			return selection;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

		@Override
		public void disable() {
			state = disabledState;
			enabledChange.emit();
		}

		@Override
		public void enable() {
			throw new UnsupportedOperationException("Already enabled");
		}
	};

	private final State<T> disabledState = new State<T>() {
		@Override
		public void select(T item) {
			throw new IllegalStateException("Disabled");
		}

		@Override
		public boolean hasSelection() {
			return selection != null;
		}

		@Override
		public T deselect() {
			throw new IllegalStateException("Disabled");
		}

		@Override
		public T selection() {
			return selection;
		}

		@Override
		public boolean isEnabled() {
			return false;
		}

		@Override
		public void disable() {
			throw new UnsupportedOperationException("Already disabled");
		}

		@Override
		public void enable() {
			state = enabledState;
			enabledChange.emit();
		}

	};

	private State<T> state = enabledState;

	private UnitSignal enabledChange = new UnitSignal();
	
	private SingleSelectionModel() {
	}

	public boolean hasSelection() {
		return state.hasSelection();
	}

	public void select(T item) {
		state.select(item);
	}

	public T deselect() {
		return state.deselect();
	}

	public T selection() {
		return state.selection();
	}

	public UnitSignal onChange() {
		return change;
	}

	public boolean isEnabled() {
		return state.isEnabled();
	}

	public SingleSelectionModel<T> disableSelectionChange() {
		state.disable();
		return this;
	}

	public SingleSelectionModel<T> enableSelectionChange() {
		state.enable();
		return this;
	}
	
	public UnitSignal enabledChange(){
		return enabledChange;
	}

}