import {AfterContentInit, Component, Inject, NgZone, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {PoiModel} from "../../models/poi.model";
import {LocationModel} from "../../models/LocationModel";
import {MarkerModel} from "../../models/marker.model";
import {MapsAPILoader} from "@agm/core";
import {LocationPipe} from "../../pipes/location.pipe";

@Component({
  selector: 'app-map-dialog',
  templateUrl: './map-dialog.component.html',
  styleUrls: ['./map-dialog.component.css']
})
export class MapDialogComponent implements AfterContentInit{
  latitude: number;
  longitude: number;
  zoom: number = 16;
  marker: MarkerModel | null;
  constructor(@Inject(MAT_DIALOG_DATA) private data: LocationModel,
              private mapsAPILoader: MapsAPILoader,
              private ngZone: NgZone,
              public locationPipe: LocationPipe) {
    this.latitude = data.latitude;
    this.longitude = data.longitude;
  }

  ngAfterContentInit(): void {
    this.mapsAPILoader.load().then(() => {
      this.setMarker(this.latitude, this.longitude);
    });
  }

  setMarker(latitude: number, longitude: number) {
    this.marker = {
      latitude: latitude,
      longitude: longitude,
      icon: './assets/svg/poi-map-point.svg',
      poiId: ""
    };
  }

}
