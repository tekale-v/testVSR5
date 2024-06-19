package com.pg.beans;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class createProject implements Serializable {
	private List<Project> Projects = new LinkedList<Project>();
	
	public void addProject(Project iProj) {
		Projects.add(iProj);
	}
	public List<Project> getProjects(){
		return Projects;
	}
	public void clearProjects() {
		Projects.clear();
	}
}
