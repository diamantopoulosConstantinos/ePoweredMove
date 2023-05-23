import {AfterContentInit, Component, OnInit} from '@angular/core';
import {ChargingStationService} from "../../services/charging.station.service";
import {AlertComponent} from "../alert/alert.component";
import {ChargingStationModel} from "../../models/charging-station.model";
import {AuthService} from "../../services/auth.service";
import {LocationPipe} from "../../pipes/location.pipe";
import {MapsAPILoader} from "@agm/core";
import {MarkerModel} from "../../models/marker.model";
import {PoiService} from "../../services/poi.service";
import {PoiModel} from "../../models/poi.model";
import {BooleanVariablePipe} from "../../pipes/boolean.variable.pipe";
import {KwhPricePipe} from "../../pipes/kwh.price.pipe";
import {PaymentTypePipe} from "../../pipes/payment.type.pipe";
import {ChargingStationDialogComponent} from "../charging-station-dialog/charging-station-dialog.component";
import {PreviewPlugsDialogComponent} from "../preview-plugs-dialog/preview-plugs-dialog.component";
import {MatDialog} from "@angular/material/dialog";

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent implements OnInit, AfterContentInit{
  pois: PoiModel[] = [];
  selectedPoi: PoiModel | undefined = undefined;
  latitude: number;
  longitude: number;
  zoom: number;
  markers: MarkerModel[] = [];
  constructor(private poiService: PoiService,
              private alert: AlertComponent,
              private authService: AuthService,
              public locationPipe: LocationPipe,
              private mapsAPILoader: MapsAPILoader,
              public booleanVariablePipe: BooleanVariablePipe,
              public kwhPricePipe: KwhPricePipe,
              private chargingStationService: ChargingStationService,
              public paymentTypePipe: PaymentTypePipe,
              public dialog: MatDialog,) {
    this.resetMap();
  }

  ngOnInit() {
    this.fetchPois();
  }

  ngAfterContentInit () {
    this.mapsAPILoader.load().then(() => {
      this.setCurrentLocation();
    });
  }

  private setCurrentLocation() {
    if ('geolocation' in navigator) {
      navigator.geolocation.getCurrentPosition((position) => {
        this.latitude = position.coords.latitude;
        this.longitude = position.coords.longitude;
        this.zoom = 16;
      });
    }
  }

  private fetchPois(){
    this.poiService.getPois().subscribe({
      next: pois => {
        if(pois){
          this.pois = pois;
          this.setMarkers();
        }
      },
      error: err => {
        this.alert.failureMessage("Error occurred while fetching points of interest.");
      }
    });
  }

  private getChargingStationImage(chargingStation: ChargingStationModel) {
    if(chargingStation && chargingStation.imageId){
      this.chargingStationService.getChargingStationImage(chargingStation.imageId).subscribe({
        next: imageUrl => {
          if(imageUrl && this.selectedPoi && this.selectedPoi.chargingStationObj){
            this.selectedPoi.chargingStationObj.imageUrl  = imageUrl;
          }
        }
      })
    }
  }

  resetMap(){
    this.latitude = 37.9908997;
    this.longitude = 23.70332;
    this.zoom = 7;
  }

  setMarkers() {
    this.pois.forEach(poi => {
      this.markers.push({
        latitude: poi.latitude,
        longitude: poi.longitude,
        icon: './assets/svg/poi-map-point.svg',
        poiId: poi.id
      });
    });
  }

  markerSelected(marker: MarkerModel) {
    this.selectedPoi = this.pois.find(poi => poi.id === marker.poiId);
    if(this.selectedPoi && this.selectedPoi.chargingStationObj){
      this.getChargingStationImage(this.selectedPoi.chargingStationObj);
    }
  }

  showPlugsDialog(selectedPoi: PoiModel) {
    if(selectedPoi && selectedPoi.chargingStationObj){
      this.dialog.open(PreviewPlugsDialogComponent, {
        width: '60%',
        height: '80%',
        enterAnimationDuration: 1000,
        exitAnimationDuration: 200,
        data: selectedPoi.chargingStationObj
      });
    }
  }
}
