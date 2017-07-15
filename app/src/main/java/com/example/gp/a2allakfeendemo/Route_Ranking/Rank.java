package com.example.gp.a2allakfeendemo.Route_Ranking;

import com.example.gp.a2allakfeendemo.Controller;
import com.example.gp.a2allakfeendemo.Route_Calculation.Route;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Eman on 07/07/2017.
 */
public class Rank {
    Route route;
    int Transitions;
    float Time;
    int Rate;
    public Rank (Route route, int Rate) throws JSONException {
        this.route = route;
        this.Rate = Math.round((4/5)*(5-Rate));
        this.Time = 0;
        this.Transitions = route.NoOfTransition;
        CalculateRank();
    }
    public void CalculateTime() throws JSONException {
        ArrayList<Integer> BusIndices = new ArrayList<>();
        ArrayList<Integer> WalkIndices = new ArrayList<>();
        Controller control = new Controller();
        for (int i = 0; i < route.Lines.size() - 1; i++) {
            if (route.Lines.get(i).line.type == 0)
                WalkIndices.add(i);
            else if (route.Lines.get(i).line.type == 1){
                if (route.Lines.get(i+1).line.type == 1 && !route.Lines.get(i).line.line.equals(route.Lines.get(i+1).line.line)){
                    this.Time += 0.083; //5 minutes
                }
                else {
                    this.Time += 0.05; //3 minutes
                }
            }
            else if (route.Lines.get(i).line.type == 2)
                BusIndices.add(i);
        }
        //check for last index
        if (route.Lines.get(route.Lines.size() - 1).line.type == 0)
            WalkIndices.add(route.Lines.size() - 1);
        else if (route.Lines.get(route.Lines.size() - 1).line.type == 1)
            this.Time += 0.05; //3 minutes
        else
            BusIndices.add(route.Lines.size() - 1);

        if (BusIndices.size() > 0) {
            ArrayList<LatLng> BusStations = new ArrayList<>();
            LatLng TmpLatLng;
            boolean StartingStation = true;
            if (BusIndices.size() == 1) {
                TmpLatLng = new LatLng(route.Stations.get(BusIndices.get(0)).Latit, route.Stations.get(BusIndices.get(0)).Longit);
                BusStations.add(TmpLatLng);
                TmpLatLng = new LatLng(route.Stations.get(BusIndices.get(0)+1).Latit, route.Stations.get(BusIndices.get(0)+1).Longit);
                BusStations.add(TmpLatLng);
                control.GetTime(BusStations,true);
                while (control.time_result == null)
                    continue;
                this.Time += ParsingTime(control.time_result,true);
                BusStations.clear();
            }
            else {
                for (int i = 0; i < BusIndices.size() - 1; i++) {
                    if (StartingStation) {
                        TmpLatLng = new LatLng(route.Stations.get(BusIndices.get(i)).Latit, route.Stations.get(BusIndices.get(i)).Longit);
                        BusStations.add(TmpLatLng);
                        TmpLatLng = new LatLng(route.Stations.get(BusIndices.get(i) + 1).Latit, route.Stations.get(BusIndices.get(i) + 1).Longit);
                        BusStations.add(TmpLatLng);
                        StartingStation = false;
                    } else {
                        TmpLatLng = new LatLng(route.Stations.get(BusIndices.get(i) + 1).Latit, route.Stations.get(BusIndices.get(i) + 1).Longit);
                        BusStations.add(TmpLatLng);
                    }
                    if (BusIndices.get(i) == BusIndices.get(i + 1) - 1) {
                        continue;
                    } else {
                        StartingStation = true;
                        control.GetTime(BusStations, true);
                        while (control.time_result == null)
                            continue;
                        this.Time += ParsingTime(control.time_result,true);
                        BusStations.clear();
                    }
                }
                if (BusIndices.get(BusIndices.size() - 1) == BusIndices.get(BusIndices.size()-2 ) + 1 ){
                    TmpLatLng = new LatLng(route.Stations.get(BusIndices.get(BusIndices.size() - 1) + 1).Latit, route.Stations.get(BusIndices.get(BusIndices.size() - 1) + 1).Longit);
                    BusStations.add(TmpLatLng);
                }
                else {
                    TmpLatLng = new LatLng(route.Stations.get(BusIndices.get(BusIndices.size() - 1)).Latit, route.Stations.get(BusIndices.get(BusIndices.size() - 1)).Longit);
                    BusStations.add(TmpLatLng);
                    TmpLatLng = new LatLng(route.Stations.get(BusIndices.get(BusIndices.size() - 1) + 1).Latit, route.Stations.get(BusIndices.get(BusIndices.size() - 1) + 1).Longit);
                    BusStations.add(TmpLatLng);
                }
                StartingStation = true;
                control.GetTime(BusStations, true);
                while (control.time_result == null)
                    continue;
                this.Time += ParsingTime(control.time_result,true);
                BusStations.clear();
            }
        }

        if (WalkIndices.size() > 0) {
            ArrayList<LatLng> WalkStations = new ArrayList<>();
            LatLng TmpLatLng;
            if (WalkIndices.size() == 1) {
                TmpLatLng = new LatLng(route.Stations.get(WalkIndices.get(0)).Latit, route.Stations.get(WalkIndices.get(0)).Longit);
                WalkStations.add(TmpLatLng);
                TmpLatLng = new LatLng(route.Stations.get(WalkIndices.get(0)+1).Latit, route.Stations.get(WalkIndices.get(0)+1).Longit);
                WalkStations.add(TmpLatLng);
                control.GetTime(WalkStations,false);
                while (control.time_result == null)
                    continue;
                this.Time += ParsingTime(control.time_result,false);
                WalkStations.clear();
            }
            else {
                for (int i = 0; i < WalkIndices.size(); i++) {
                    TmpLatLng = new LatLng(route.Stations.get(WalkIndices.get(i)).Latit, route.Stations.get(WalkIndices.get(i)).Longit);
                    WalkStations.add(TmpLatLng);
                    TmpLatLng = new LatLng(route.Stations.get(WalkIndices.get(i)+1).Latit, route.Stations.get(WalkIndices.get(i)+1).Longit);
                    WalkStations.add(TmpLatLng);
                    control.GetTime(WalkStations, false);
                    while (control.time_result == null)
                        continue;
                    this.Time += ParsingTime(control.time_result, false);
                    WalkStations.clear();
                }
            }
        }
    }

    public float ParsingTime (String JsonResult, boolean bus) throws JSONException {
        JSONObject jsonObj = new JSONObject(JsonResult);
        long timeAPI;
        if (!bus)
            timeAPI = jsonObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getLong("value");
        else
            timeAPI = jsonObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration_in_traffic").getLong("value");
        //Convert to hours
        float time = (float)timeAPI/3600;
        //return as float
        return time;
    }

    public void CalculateRank() throws JSONException {
        for (int i = 0 ; i < route.Lines.size()-1; i++){
            if ( route.Lines.get(i).line.type == 0  && route.Lines.get(i+1).line.type == 0){
                return;
            }
        }
        CalculateTime();
        //Rank Equation
        route.Rank = (Transitions*0.4) + (Time*0.5) + (Rate*0.1);
    }
}
