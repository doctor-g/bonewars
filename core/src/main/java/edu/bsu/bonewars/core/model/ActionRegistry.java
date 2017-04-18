package edu.bsu.bonewars.core.model;

import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import com.google.common.collect.Maps;

import edu.bsu.bonewars.core.actions.AcquireSiteAction;
import edu.bsu.bonewars.core.actions.AnalyzeAction;
import edu.bsu.bonewars.core.actions.AnalyzedPublishAction;
import edu.bsu.bonewars.core.actions.ExcavateAction;
import edu.bsu.bonewars.core.actions.HireWorkerAction;
import edu.bsu.bonewars.core.actions.RaiseFundsAction;
import edu.bsu.bonewars.core.actions.UnanalyzedPublishedAction;

public class ActionRegistry {

	public static ActionRegistry create() {
		return new ActionRegistry();
	}
	
	private Map<Site, ExcavateAction> excavationActionRegistry = Maps.newHashMap();
	private Map<Site, AcquireSiteAction> acquireSiteActionRegistry = Maps.newHashMap();
	private Map<FossilStack, AnalyzeAction> analyzeActionRegistry = Maps.newHashMap();
	private Map<FossilStack, AnalyzedPublishAction> analyzedPublishActionRegistry = Maps.newHashMap();
	private Map<FossilStack, UnanalyzedPublishedAction> unanalyzedPublishActionRegistry = Maps.newHashMap();
	private Map<Player, HireWorkerAction> hireWorkerActionRegistry = Maps.newHashMap();
	private Map<Funds, RaiseFundsAction> raiseFundsActionRegistry = Maps.newHashMap();
	
	
	private ActionRegistry(){}

	public void registerExcavateAction(ExcavateAction excavateAction) {
		excavationActionRegistry.put(excavateAction.site(), excavateAction);
	}
	
	public boolean hasExcavationActionRegisteredForSite(Site site){
		return excavationActionRegistry.containsKey(site);
	}

	public ExcavateAction getExcavateActionForSite(Site site) {
		checkState(hasExcavationActionRegisteredForSite(site));
		return excavationActionRegistry.get(site);
	}

	public void registerAcquireSiteAction(AcquireSiteAction acquireSiteAction) {
		acquireSiteActionRegistry.put(acquireSiteAction.site, acquireSiteAction);
	}
	public boolean hasAcquireSiteActionRegisteredForSite(Site site){
		return acquireSiteActionRegistry.containsKey(site);
	}

	public AcquireSiteAction getAcquireSiteActionForSite(Site site) {
		checkState(hasAcquireSiteActionRegisteredForSite(site));
		return acquireSiteActionRegistry.get(site);
	}

	public void registerAnalyzeAction(AnalyzeAction analyzeAction) {
		analyzeActionRegistry.put(analyzeAction.fossilStack(), analyzeAction);
	}
	
	public boolean hasAnalyzeActionRegisteredForFossilStack(FossilStack fossilStack){
		return analyzeActionRegistry.containsKey(fossilStack);
	}

	public AnalyzeAction getAnalyzeActionForFossilStack(FossilStack fossilStack) {
		checkState(hasAnalyzeActionRegisteredForFossilStack(fossilStack));
		return analyzeActionRegistry.get(fossilStack);
	}
	
	public void registerAnalyzedPublishAction(AnalyzedPublishAction analyzedPublishAction) {
		analyzedPublishActionRegistry.put(analyzedPublishAction.fossilStack(), analyzedPublishAction);
	}
	
	public boolean hasAnalyzedPublishActionRegisteredForFossil(FossilStack fossilStack){
		return analyzedPublishActionRegistry.containsKey(fossilStack);
	}

	public AnalyzedPublishAction getAnalyzedPublishActionForFossilStack(FossilStack fossilStack) {
		checkState(hasAnalyzedPublishActionRegisteredForFossil(fossilStack));
		return analyzedPublishActionRegistry.get(fossilStack);
	}

	public void registerHireWorkerAction(HireWorkerAction hireWorkerAction) {
		hireWorkerActionRegistry.put(hireWorkerAction.player(), hireWorkerAction);
		
	}

	public HireWorkerAction getHireWorkerAction(Player player) {
		checkState(hasHireWorkerActionRegisteredForWorker(player));
		return hireWorkerActionRegistry.get(player);
	}

	public boolean hasHireWorkerActionRegisteredForWorker(Player player) {
		return hireWorkerActionRegistry.containsKey(player);
	}

	public void registerRaiseFundsAction(Funds funds, RaiseFundsAction raiseFundsAction) {
		raiseFundsActionRegistry.put(funds, raiseFundsAction);
	}

	public void registerUnanalyzedPublishAction(UnanalyzedPublishedAction unanalyzedPublishedAction) {
		unanalyzedPublishActionRegistry.put(unanalyzedPublishedAction.fossilStack(), unanalyzedPublishedAction);
	}
	
	public boolean hasUnanalyzedPublishActionRegisteredForFossil(FossilStack fossilStack){
		return unanalyzedPublishActionRegistry.containsKey(fossilStack);
	}
	
	public UnanalyzedPublishedAction getUnanalyzedPublishActionForFossilStack(FossilStack fossilStack) {
		checkState(hasUnanalyzedPublishActionRegisteredForFossil(fossilStack));
		return unanalyzedPublishActionRegistry.get(fossilStack);
	}
	
	public boolean areAnyActionsAvailableForSmallWorkerOfCurrentPlayer(){
		for(Worker smallWorker: Game.currentGame().currentPlayer().get().littleWorkerCollection()){
			if(smallWorker.isReadyForWork().get()){
				return evaluateLittleWorkerActionAvailability();
			}
		}
		return false;
	}

	private boolean evaluateLittleWorkerActionAvailability() {
		for(ExcavateAction action: excavationActionRegistry.values()){
			if(action.isAvailableDisreguardingPlayerSelection()){
				return true;
			}
		}
		for(AnalyzeAction action: analyzeActionRegistry.values()){
			if(action.isAvailableDisreguardingPlayerSelection()){
				return true;
			}
		}
		return false;
	}
}
