package edu.bsu.bonewars.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import react.UnitSlot;

public class SingleSelectionModelTest {

	private SingleSelectionModel<Object> model;

	@Before
	public void setUp() {
		model = SingleSelectionModel.create();
	}

	@Test
	public void testNoSelection() {
		assertFalse(model.hasSelection());
	}

	@Test
	public void testHasSelection_itemSelected() {
		model.select("Foo");
		assertTrue(model.hasSelection());
	}

	@Test
	public void testNoSelect_unselected() {
		model.select("Foo");
		model.deselect();
		assertFalse(model.hasSelection());
	}

	@Test
	public void testGetSelection() {
		String s = "Foo";
		model.select(s);
		assertEquals(s, model.selection());
	}

	@Test(expected = IllegalStateException.class)
	public void testThrowsException_gettingSelectionWithoutSelection() {
		model.selection();
	}

	@Test(expected = IllegalStateException.class)
	public void testThrowsException_deselectingWithoutSelection() {
		model.deselect();
	}

	@Test(expected = NullPointerException.class)
	public void testThrowsException_selectingNull() {
		model.select(null);
	}

	@Test
	public void testNotificationOfSelection() {
		UnitSlot mockSlot = mock(UnitSlot.class);
		model.onChange().connect(mockSlot);
		model.select(mock(Object.class));
		verify(mockSlot).onEmit();
	}
	
	@Test
	public void testIsEnabled_start() {
		assertTrue(model.isEnabled());
	}
	
	@Test
	public void testIsEnabled_afterCallingDisabled(){
		model.disableSelectionChange();
		assertFalse(model.isEnabled());
	}

}
