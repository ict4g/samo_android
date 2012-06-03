package eu.fbk.ict4g.samo.models;

import java.util.ArrayList;
import java.util.List;

public class Campaign {
	
	private long id;
	
	private String title;
	private String description;
	private String dateFrom, dateTo;
	
	private List<Assessment> assessments;
	private List<Indicator> indicators;
	private List<Target> targets;
	
	/**
	 * 
	 */
	public Campaign() {
		assessments = new ArrayList<Assessment>();
		indicators = new ArrayList<Indicator>();
		targets = new ArrayList<Target>();
	}
	
	public void addAssessment(Assessment assessment) {
		this.assessments.add(assessment);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<Assessment> getAssessments() {
		return assessments;
	}

	public void setAssessments(List<Assessment> assessments) {
		for (Assessment assessment : assessments) {
			this.assessments.add(assessment);
		}
//		this.assessments = assessments;
	}

	public List<Indicator> getIndicators() {
		return indicators;
	}

	public void setIndicators(List<Indicator> indicators) {
		for (Indicator indicator : indicators) {
			this.indicators.add(indicator);
		}
		//this.indicators = indicators;
	}

	public List<Target> getTargets() {
		return targets;
	}

	public void setTargets(List<Target> targets) {
		for (Target target : targets) {
			this.targets.add(target);
		}
//		this.targets = targets;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	@Override
	public String toString() {
		return "Campaign [id=" + id + "]";
	}

}
