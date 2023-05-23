import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import { HashLocationStrategy, LocationStrategy } from '@angular/common';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { PageNotFoundComponent } from './components/page-not-found/page-not-found.component';
import { MainComponent } from './components/main/main.component';
import { SignInComponent } from './components/sign-in/sign-in.component';
import { SignUpComponent } from './components/sign-up/sign-up.component';
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatIconModule} from "@angular/material/icon";
import {MatMenuModule} from "@angular/material/menu";
import {MatButtonToggleModule} from "@angular/material/button-toggle";
import { environment } from '../environments/environment';
import {AuthService} from "./services/auth.service";
import {RouterLink} from "@angular/router";
import {AppRoutingModule} from "./app-routing.module";
import { NavbarComponent } from './components/navbar/navbar.component';
import { LoaderComponent } from './components/loader/loader.component';
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import { HomeComponent } from './components/home/home.component';
import {UniversalAppInterceptor} from "./interceptors/universal-app-interceptor";
import { MomentDateAdapter } from '@angular/material-moment-adapter';
import {
  DateAdapter,
  MAT_DATE_LOCALE,
} from '@angular/material/core';
import { AlertComponent } from './components/alert/alert.component';
import {MatSnackBar} from "@angular/material/snack-bar";
import { ProfileComponent } from './components/profile/profile.component';
import { MapComponent } from './components/map/map.component';
import { ReservationsComponent } from './components/reservations/reservations.component';
import { PoisComponent } from './components/pois/pois.component';
import {IsAuthGuard} from "./guards/is.auth.guard";
import {MatCardModule} from "@angular/material/card";
import {MAT_FORM_FIELD_DEFAULT_OPTIONS, MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButtonModule} from "@angular/material/button";
import {AngularFireModule} from "@angular/fire/compat";
import {AngularFireAuthModule, PERSISTENCE} from "@angular/fire/compat/auth";
import {AngularFireStorageModule} from "@angular/fire/compat/storage";
import { AccountsComponent } from './components/accounts/accounts.component';
import {MatDividerModule} from "@angular/material/divider";
import {MatTooltipModule} from "@angular/material/tooltip";
import {UserService} from "./services/user.service";
import {IsNotAuthGuard} from "./guards/is.not.auth.guard";
import {MatTableModule} from "@angular/material/table";
import {MatPaginatorModule} from "@angular/material/paginator";
import {LoaderService} from "./services/loader.service";
import {PoiService} from "./services/poi.service";
import {BooleanVariablePipe} from "./pipes/boolean.variable.pipe";
import { PoiAnimationDialogComponent } from './components/poi-animation-dialog/poi-animation-dialog.component';
import {MatDialog, MatDialogModule} from "@angular/material/dialog";
import {MatStepperModule} from "@angular/material/stepper";
import {MatSlideToggleModule} from "@angular/material/slide-toggle";
import {AgmCoreModule} from "@agm/core";
import {ChargingStationService} from "./services/charging.station.service";
import { YesNoQuestionDialogComponent } from './components/yes-no-question.dialog/yes-no-question.dialog.component';
import {KwhPricePipe} from "./pipes/kwh.price.pipe";
import {LocationPipe} from "./pipes/location.pipe";
import {DatePipe} from "./pipes/date.pipe";
import { MapDialogComponent } from './components/map-dialog/map-dialog.component';
import { ChargingStationDialogComponent } from './components/charging-station-dialog/charging-station-dialog.component';
import {MatListModule} from "@angular/material/list";
import {PlugTypeService} from "./services/plug.type.service";
import {AvailabilityPipe} from "./pipes/availability.pipe";
import { PlugAnimationDialogComponent } from './components/plug-animation-dialog/plug-animation-dialog.component';
import {MatSelectModule} from "@angular/material/select";
import {CdkFixedSizeVirtualScroll} from "@angular/cdk/scrolling";
import { PreviewPlugDialogComponent } from './components/preview-plug-dialog/preview-plug-dialog.component';
import {TeslaCompatiblePipe} from "./pipes/tesla.compatible.pipe";
import {CurrentTypePipe} from "./pipes/current.type.pipe";
import { ReviewsDialogComponent } from './components/reviews-dialog/reviews-dialog.component';
import {ReviewService} from "./services/review.service";
import {ReviewStatusPipe} from "./pipes/review.status.pipe";
import {StarRatingModule} from "angular-star-rating";
import {TimePipe} from "./pipes/time.pipe";
import {MatSidenavModule} from "@angular/material/sidenav";
import {PaymentTypePipe} from "./pipes/payment.type.pipe";
import { PreviewPlugsDialogComponent } from './components/preview-plugs-dialog/preview-plugs-dialog.component';
import {RolesGuard} from "./guards/roles.guard";
import {UserRolesPipe} from "./pipes/user.roles.pipe";

@NgModule({
  declarations: [
    PageNotFoundComponent,
    MainComponent,
    SignInComponent,
    SignUpComponent,
    NavbarComponent,
    LoaderComponent,
    HomeComponent,
    AlertComponent,
    ProfileComponent,
    MapComponent,
    ReservationsComponent,
    PoisComponent,
    AccountsComponent,
    BooleanVariablePipe,
    PoiAnimationDialogComponent,
    YesNoQuestionDialogComponent,
    KwhPricePipe,
    LocationPipe,
    DatePipe,
    TeslaCompatiblePipe,
    CurrentTypePipe,
    ReviewStatusPipe,
    PaymentTypePipe,
    UserRolesPipe,
    TimePipe,
    MapDialogComponent,
    ChargingStationDialogComponent,
    AvailabilityPipe,
    PlugAnimationDialogComponent,
    PreviewPlugDialogComponent,
    ReviewsDialogComponent,
    PreviewPlugsDialogComponent,
  ],
    imports: [
        HttpClientModule,
        AppRoutingModule,
        BrowserModule,
        BrowserAnimationsModule,
        MatToolbarModule,
        MatIconModule,
        MatMenuModule,
        MatButtonToggleModule,
        AngularFireModule.initializeApp(environment.firebase),
        AngularFireAuthModule,
        AngularFireStorageModule,
        RouterLink,
        MatProgressSpinnerModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        MatButtonModule,
        MatDividerModule,
        MatTooltipModule,
        MatTableModule,
        MatPaginatorModule,
        MatDialogModule,
        MatStepperModule,
        MatSlideToggleModule,
        AgmCoreModule.forRoot({
            apiKey: environment.googleApiKey,
            libraries: ['places']
        }),
        MatListModule,
        MatSelectModule,
        CdkFixedSizeVirtualScroll,
        StarRatingModule.forRoot(),
        MatSidenavModule,
        FormsModule,
    ],
  providers: [
    AuthService,
    UserService,
    LoaderService,
    PoiService,
    ChargingStationService,
    PlugTypeService,
    ReviewService,
    BooleanVariablePipe,
    KwhPricePipe,
    LocationPipe,
    DatePipe,
    TimePipe,
    CurrentTypePipe,
    PaymentTypePipe,
    UserRolesPipe,
    TeslaCompatiblePipe,
    ReviewStatusPipe,
    LoaderComponent,
    AlertComponent,
    MatSnackBar,
    IsAuthGuard,
    IsNotAuthGuard,
    MainComponent,
    MatDialog,
    PoisComponent,
    AvailabilityPipe,
    RolesGuard,
    {provide: LocationStrategy, useClass: HashLocationStrategy}, //Diamantopoulos for 404 when refreshing on NGINX
    { provide: DateAdapter, useClass: MomentDateAdapter },
    { provide: MAT_DATE_LOCALE, useValue: 'el-GR' },
    { provide: HTTP_INTERCEPTORS, useClass: UniversalAppInterceptor, multi: true },
    { provide: PERSISTENCE, useValue: 'local'}, //keep firebase session even if page is refreshed
    {provide: MAT_FORM_FIELD_DEFAULT_OPTIONS, useValue: {floatLabel: 'auto'}} //form elements floating
  ],
  bootstrap: [MainComponent]
})
export class AppModule { }
