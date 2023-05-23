import {AfterContentInit, AfterViewInit, Component, ElementRef, Inject, NgZone, OnInit, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {MapsAPILoader, MouseEvent} from "@agm/core";
import {MarkerModel} from "../../models/marker.model";
import {AlertComponent} from "../alert/alert.component";
import {PoiModel} from "../../models/poi.model";
import {ChargingStationModel} from "../../models/charging-station.model";
import {UserModel} from "../../models/user.model";
import {AuthService} from "../../services/auth.service";
import {PoiService} from "../../services/poi.service";
import {ChargingStationService} from "../../services/charging.station.service";
import {PaymentMethodModel} from "../../models/payment-method.model";
import {PaymentTypeEnum} from "../../models/enums/payment-type.enum";
import {KwhPricePipe} from "../../pipes/kwh.price.pipe";
import {LocationPipe} from "../../pipes/location.pipe";


@Component({
  selector: 'app-poi-animation-dialog',
  templateUrl: './poi-animation-dialog.component.html',
  styleUrls: ['./poi-animation-dialog.component.css']
})

export class PoiAnimationDialogComponent implements AfterContentInit, OnInit{
  poiFormGroup: FormGroup = new FormGroup({
    parking: new FormControl(false, [Validators.required]),
    illumination: new FormControl(false, [Validators.required]),
    wc: new FormControl(false, [Validators.required]),
    shopping : new FormControl(false, [Validators.required]),
    food: new FormControl(false, [Validators.required]),
    phone: new FormControl(null, [Validators.required, Validators.pattern('[- +()0-9]{6,12}')]),
  });

  chargingStationFormGroup: FormGroup = new FormGroup({
    pricePerKwh: new FormControl(null, [Validators.required, Validators.pattern('^\\d+\\.\\d{0,3}$')]),
    autoAccept: new FormControl(false, [Validators.required]),
    barcodeEnabled: new FormControl(false, [Validators.required]),
    apiEnabled : new FormControl(false, [Validators.required]),
  });

  paymentMethodFormGroup: FormGroup = new FormGroup({
    app: new FormControl(false, [Validators.required]),
    paypal: new FormControl(false, [Validators.required]),
    ebanking: new FormControl(false, [Validators.required]),
    locally : new FormControl(false, [Validators.required]),
  });

  latitude: number;
  longitude: number;
  zoom: number;
  marker: MarkerModel | null;
  //private geoCoder: Geocoder;
  imageSrc: string | null = null;
  imageFile: File | null;
  @ViewChild('locationInput', { static: false }) locationInput: ElementRef<HTMLInputElement>;
  @ViewChild('fileInput', { static: false }) fileInput: ElementRef<HTMLInputElement>;
  constructor(public dialogRef: MatDialogRef<PoiAnimationDialogComponent>,
              private mapsAPILoader: MapsAPILoader,
              private ngZone: NgZone,
              private alert: AlertComponent,
              private authService: AuthService,
              private poiService: PoiService,
              private chargingStationService: ChargingStationService,
              @Inject(MAT_DIALOG_DATA) private data: PoiModel,
              private kwhPricePipe: KwhPricePipe,
              public locationPipe: LocationPipe) {}

  ngOnInit() {
        this.resetMap();
  }

  ngAfterContentInit () {
    //load Places Autocomplete
    this.mapsAPILoader.load().then(() => {

      //is editing mode
      if(this.data){
        this.setEditingPoi();
      }
      else{
        this.resetMap();
      }
      //this.geoCoder = new google.maps.Geocoder;

      let autocomplete = new google.maps.places.Autocomplete(this.locationInput.nativeElement);
      autocomplete.addListener("place_changed", () => {
        this.ngZone.run(() => {
          //get the place result
          let place: google.maps.places.PlaceResult = autocomplete.getPlace();

          //verify result
          if (place.geometry === undefined || place.geometry === null) {
            return;
          }

          //set latitude, longitude and zoom
          this.latitude = place.geometry.location.lat();
          this.longitude = place.geometry.location.lng();
          this.zoom = 16;
          this.setMarker(this.latitude, this.longitude);
        });
      });
    });
  }

  // private setCurrentLocation() {
  //   if ('geolocation' in navigator) {
  //     navigator.geolocation.getCurrentPosition((position) => {
  //       this.latitude = position.coords.latitude;
  //       this.longitude = position.coords.longitude;
  //       this.zoom = 16;
  //       this.setMarker(this.latitude, this.longitude);
  //     });
  //   }
  // }
  mapClicked($event: MouseEvent) {
    if($event?.coords?.lat && $event?.coords?.lng){
      this.setMarker($event.coords.lat, $event.coords.lng);
      this.zoom = 16;
    }
  }

  setMarker(latitude: number, longitude: number) {
    this.marker = {
      latitude: latitude,
      longitude: longitude,
      icon: './assets/svg/poi-map-point.svg',
      poiId: ""
    };
  }

  // getAddress(latitude: number, longitude: number) {
  //   this.geoCoder.geocode({ 'location': { lat: latitude, lng: longitude } }, (results, status) => {
  //     if (status === 'OK') {
  //       if (results[0]) {
  //         this.zoom = 12;
  //       } else {
  //         this.alert.notificationMessage('No results found');
  //       }
  //     } else {
  //       this.alert.failureMessage("Error occurred while searching location");
  //     }
  //   });
  // }

  resetMap(){
    this.latitude = 37.9908997;
    this.longitude = 23.70332;
    this.zoom = 16;
    this.marker = null;
  }
  readURL(event: any): void {
    this.removeImage();
    if (event?.target.files && event.target.files[0]) {
      const file = event.target.files[0];
      this.imageFile = file;
      const reader = new FileReader();
      reader.onload = () => this.imageSrc = reader.result as string;

      reader.readAsDataURL(file);
    }
  }

  removeImage() {
    this.imageFile = null;
    this.imageSrc = null;
    this.fileInput.nativeElement.src = '';
  }

  submit() {
    const poi: PoiModel = {} as PoiModel;
    if(this.poiFormGroup.valid && this.chargingStationFormGroup.valid){
      if(!this.marker?.latitude || !this.marker?.longitude){
        this.alert.notificationMessage("You have to select a location before you continue");
        return;
      }
      poi.wc = this.poiFormGroup.get('wc')?.value as boolean;
      poi.illumination = this.poiFormGroup.get('illumination')?.value as boolean;
      poi.food = this.poiFormGroup.get('food')?.value as boolean;
      poi.parking = this.poiFormGroup.get('parking')?.value as boolean;
      poi.shopping = this.poiFormGroup.get('shopping')?.value as boolean;
      poi.phone = this.poiFormGroup.get('phone')?.value;
      poi.latitude = this.marker?.latitude!!;
      poi.longitude = this.marker?.longitude!!;

      const chargingStation: ChargingStationModel = {} as ChargingStationModel;
      chargingStation.apiEnabled = this.chargingStationFormGroup.get('apiEnabled')?.value as boolean;
      chargingStation.autoAccept = this.chargingStationFormGroup.get('autoAccept')?.value as boolean;
      chargingStation.barcodeEnabled = this.chargingStationFormGroup.get('barcodeEnabled')?.value as boolean;
      chargingStation.pricePerKwh = this.chargingStationFormGroup.get('pricePerKwh')?.value as number;
      poi.chargingStationObj = chargingStation;

      const user: UserModel = {} as UserModel;
      user.id = this.authService.loggedInUser?.uid!!;
      poi.userObj = user;

      const paymentMethods: PaymentMethodModel[] = [] as PaymentMethodModel[];
      Object.keys(this.paymentMethodFormGroup.controls).forEach(key => {
        if(this.paymentMethodFormGroup.controls[key].value as boolean){
          const currentPaymentMethod = {} as PaymentMethodModel;
          switch (key){
            case 'app':
              currentPaymentMethod.description = PaymentTypeEnum[PaymentTypeEnum.APP]
              break;
            case 'paypal':
              currentPaymentMethod.description = PaymentTypeEnum[PaymentTypeEnum.PAYPAL]
              break;
            case 'ebanking':
              currentPaymentMethod.description = PaymentTypeEnum[PaymentTypeEnum.EBANKING]
              break;
            case 'locally':
              currentPaymentMethod.description = PaymentTypeEnum[PaymentTypeEnum.LOCALLY]
              break;
          }
          paymentMethods.push(currentPaymentMethod);
        }
      })
      poi.paymentMethodsObj = paymentMethods;

      //is editing mode
      if(this.data){
        //set already defined ids
        poi.id = this.data.id;
        poi.chargingStationObj.id = this.data.chargingStationObj.id;
        poi.userObj.id = this.data.userObj.id;
        poi.timestamp = this.data.timestamp;
        //if had image and current image is not edited
        if(this.data.chargingStationObj.imageId && this.imageSrc && !this.imageFile){
          poi.chargingStationObj.imageId = this.data.chargingStationObj.imageId;
        }
        this.poiService.updatePoi(poi).subscribe({
          next: poiUpdated => {
            //replace image
            if(poiUpdated.chargingStationObj.id && this.imageFile){
              this.chargingStationService.addChargingStationImage(this.data.chargingStationObj.id, this.imageFile!!).subscribe({
                next: imageResponse => {
                  if(imageResponse.imageId){
                    poiUpdated.chargingStationObj.imageId = imageResponse.imageId;
                    this.resetAll();
                    this.dialogRef.close(poiUpdated);
                  }
                  else{
                    this.alert.notificationMessage("Charging Station Image could not be saved. Please try again.");
                    this.resetAll();
                    this.dialogRef.close(poiUpdated);
                  }
                },
                error: err => {
                  this.alert.failureMessage("Charging Station Image could not be saved. Please try again.");
                  this.resetAll();
                  this.dialogRef.close(poiUpdated);
                }
              });
            }
            //no change to image detected
            else{
              this.resetAll();
              this.dialogRef.close(poiUpdated);
            }
          },
          error: err => {
            this.alert.failureMessage("Error occurred while saving Point of Interest. Please try again.");
          }
        })
      }
      //new poi
      else{
        this.poiService.addPoi(poi).subscribe({
          next: poiAdded => {
            if(poiAdded.chargingStationObj.id && this.imageFile){
              this.chargingStationService.addChargingStationImage(poiAdded.chargingStationObj.id, this.imageFile!!).subscribe({
                next: imageResponse => {
                  if(imageResponse.imageId){
                    poiAdded.chargingStationObj.imageId = imageResponse.imageId;
                    this.resetAll();
                    this.dialogRef.close(poiAdded);
                  }
                  else{
                    this.alert.notificationMessage("Charging Station Image could not be saved. Please try adding image by editing Point of interest.");
                    this.resetAll();
                    this.dialogRef.close(poiAdded);
                  }
                },
                error: err => {
                  this.alert.notificationMessage("Charging Station Image could not be saved. Please try adding image by editing Point of interest.");
                  this.resetAll();
                  this.dialogRef.close(poiAdded);
                }
              });
            }
            else{
              this.resetAll();
              this.dialogRef.close(poiAdded);
            }
          },
          error: err => {
            this.alert.failureMessage("Error occurred while saving Point of Interest. Please try again.");
          }
        })
      }
    }
  }

  private resetAll(){
    this.poiFormGroup.reset();
    this.chargingStationFormGroup.reset();
    this.resetMap();
    this.imageFile = null;
    this.imageSrc = null;
  }

  private setEditingPoi() {
    this.latitude = this.data.latitude;
    this.longitude = this.data.longitude;
    this.setMarker(this.latitude, this.longitude);
    this.poiFormGroup.setValue({
      wc: this.data.wc,
      food: this.data.food,
      illumination: this.data.illumination,
      parking: this.data.parking,
      shopping: this.data.shopping,
      phone: this.data.phone
    });
    if(this.data.chargingStationObj){
      this.chargingStationFormGroup.setValue({
        apiEnabled: this.data.chargingStationObj.apiEnabled,
        autoAccept: this.data.chargingStationObj.autoAccept,
        barcodeEnabled: this.data.chargingStationObj.barcodeEnabled,
        pricePerKwh: this.kwhPricePipe.transform(this.data.chargingStationObj.pricePerKwh)
      });
      if(this.data.chargingStationObj.imageId){
        this.chargingStationService.getChargingStationImage(this.data.chargingStationObj.imageId).subscribe({
          next: imageUrl => {
            if(imageUrl){
              this.imageSrc = imageUrl as string;
            }
          },
          error: err => {
            this.alert.notificationMessage("Could not retrieve charging station image");
          }
        })
      }
      if(this.data.paymentMethodsObj){
        this.paymentMethodFormGroup.setValue({
          app: this.data.paymentMethodsObj.some(payment => payment.description === PaymentTypeEnum[PaymentTypeEnum.APP]),
          paypal: this.data.paymentMethodsObj.some(payment => payment.description === PaymentTypeEnum[PaymentTypeEnum.PAYPAL]),
          ebanking: this.data.paymentMethodsObj.some(payment => payment.description === PaymentTypeEnum[PaymentTypeEnum.EBANKING]),
          locally: this.data.paymentMethodsObj.some(payment => payment.description === PaymentTypeEnum[PaymentTypeEnum.LOCALLY]),
        })
      }
    }
  }
}
