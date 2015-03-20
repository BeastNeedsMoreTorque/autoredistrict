package geoJSON;

import java.awt.Color;
import java.io.File;
import java.util.*;

import javax.swing.JOptionPane;

import mapCandidates.Settings;

import serialization.*;
import ui.MainFrame;


public class Project extends ReflectionJSONObject<Project> {
	public String source_file = "";
	public int number_of_districts = 10;
	public int members_per_district = 1;
	public boolean area_weighted = true;
	public int initial_population = 128;
	public int simulated_elections = 4;
	
	public String primary_key_column = "";
	public String population_column = "";
	public Vector<DemographicSet> demographics = new Vector<DemographicSet>();
	
	public String active_demographic_set = "";
	public double disconnected_weight = 0.5;
	public double population_balance_weight = 0.5;
	public double border_length_weight = 0.5;
	public double voting_power_weight = 0.5;
	public double representation_weight = 0.5;
	public boolean equalize_turnout = false;

	public void post_deserialize() {
		super.post_deserialize();


		if( containsKey("equalize_turnout")) {
			Settings.adjust_vote_to_population = getString("equalize_turnout").trim().toLowerCase().equals("true");
		} else { System.out.println("equalize_turnout not found"); }
	    
		System.out.println("found keys:");
		Set<String> keys = keySet();
		for( String s : keys) {
			System.out.println(s);
		}
		if( containsKey("source_file")) {
			String source = getString("source_file").trim();
			System.out.println("source file: "+source);
			String ext = source.substring(source.length()-4).toLowerCase();
			File f = new File(source);
			if( f == null) {
				JOptionPane.showMessageDialog(MainFrame.mainframe, "File not found: "+source);
				return;
			} else if( ext.equals(".shp")) {
				MainFrame.mainframe.openShapeFile(f,true);
			} else if( ext.equals("json")) {
				MainFrame.mainframe.openGeoJson(f,true);
				
			} else {
				JOptionPane.showMessageDialog(MainFrame.mainframe, "Invalid file format: "+source);
				return;
			}
		} else { System.out.println("source_file not found"); return; }

		if( containsKey("number_of_districts")) {
			MainFrame.mainframe.textFieldNumDistricts.setText(getString("number_of_districts").trim());
			MainFrame.mainframe.textFieldNumDistricts.postActionEvent();
		} else { System.out.println("number_of_districts not found"); }
		if( containsKey("members_per_district")) {
			MainFrame.mainframe.textFieldMembersPerDistrict.setText(getString("members_per_district").trim());
			MainFrame.mainframe.textFieldMembersPerDistrict.postActionEvent();
		} else { System.out.println("members_per_district not found"); }
		
		if( containsKey("initial_population")) {
			MainFrame.mainframe.textField.setText(getString("initial_population").trim());
			MainFrame.mainframe.textField.postActionEvent();
		} else { System.out.println("initial_population not found"); }

		if( containsKey("elections_simulated")) {
			MainFrame.mainframe.textFieldElectionsSimulated.setText(getString("elections_simulated").trim());
			MainFrame.mainframe.textFieldElectionsSimulated.postActionEvent();
		} else { System.out.println("elections_simulated not found"); }

		if( containsKey("population_column")) {
			MainFrame.mainframe.comboBoxPopulation.setSelectedItem(getString("population_column").trim());
			//MainFrame.mainframe.setPopulationColumn(getString("population_column").trim());
		} else { System.out.println("population_column not found"); }
		if( containsKey("primary_key_column")) {
			MainFrame.mainframe.comboBoxPrimaryKey.setSelectedItem(getString("primary_key_column").trim());
			//MainFrame.mainframe.setPopulationColumn(getString("population_column").trim());
		} else { System.out.println("primary_key_column not found"); }
		
		if( containsKey("demographic_columns")) {
			Vector v = getVector("demographic_columns");
			Vector<String> vs = new Vector<String>();
			for( int i = 0; i < v.size(); i++) {
				vs.add((String)v.get(i));
			}
			MainFrame.mainframe.setDemographicColumns(vs);
		} else { System.out.println("demographic_columns not found"); }

		if( containsKey("disconnected_weight")) {
			MainFrame.mainframe.sliderDisconnected.setValue((int)(100.0*Double.parseDouble(getString("disconnected_weight").trim())));
		}
		if( containsKey("population_balance_weight")) {
			MainFrame.mainframe.sliderPopulationBalance.setValue((int)(100.0*Double.parseDouble(getString("population_balance_weight").trim())));
		}
		if( containsKey("border_length_weight")) {
			MainFrame.mainframe.sliderBorderLength.setValue((int)(100.0*Double.parseDouble(getString("border_length_weight").trim())));
		}
		if( containsKey("voting_power_weight")) {
			MainFrame.mainframe.sliderVotingPowerBalance.setValue((int)(100.0*Double.parseDouble(getString("voting_power_weight").trim())));
		}
		if( containsKey("representation_weight")) {
			MainFrame.mainframe.sliderRepresentation.setValue((int)(100.0*Double.parseDouble(getString("representation_weight").trim())));
		}
		if( containsKey("area_weighted")) {
			MainFrame.mainframe.chckbxAreaWeighted.setSelected(getString("area_weighted").trim().toLowerCase().equals("true"));
		}

		
		MainFrame.mainframe.featureCollection.ecology.startEvolving();
	
	}

	@Override
	public void pre_serialize() {
		voting_power_weight = Settings.voting_power_balance_weight;
		disconnected_weight = Settings.disconnected_population_weight;
		population_balance_weight = Settings.population_balance_weight;
		representation_weight = Settings.disenfranchise_weight;
		border_length_weight = Settings.geometry_weight;
		
		initial_population = Settings.population;
		number_of_districts = Settings.num_districts;
		members_per_district = Settings.members_per_district;
		simulated_elections = Settings.num_elections_simulated;
		
		equalize_turnout = Settings.adjust_vote_to_population;
		
		members_per_district = Settings.members_per_district;
		area_weighted = Settings.border_length_area_weighted;
		
		//initial_population = Integer.parseInt(((String)MainFrame.mainframe.textField.getText()).trim()));
		super.pre_serialize();
	}

	@Override
	public JSONObject instantiateObject(String key) {
		if( key.equals("demographics")) {
			return new DemographicSet();
		}
		return super.instantiateObject(key);
	}
	
}