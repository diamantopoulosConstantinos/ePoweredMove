import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {SignInComponent} from "./components/sign-in/sign-in.component";
import {PageNotFoundComponent} from "./components/page-not-found/page-not-found.component";
import {SignUpComponent} from "./components/sign-up/sign-up.component";
import {HomeComponent} from "./components/home/home.component";
import {MapComponent} from "./components/map/map.component";
import {IsAuthGuard} from "./guards/is.auth.guard";
import {ReservationsComponent} from "./components/reservations/reservations.component";
import {ProfileComponent} from "./components/profile/profile.component";
import {PoisComponent} from "./components/pois/pois.component";
import {AccountsComponent} from "./components/accounts/accounts.component";
import {IsNotAuthGuard} from "./guards/is.not.auth.guard";
import {RolesGuard} from "./guards/roles.guard";

const routes: Routes = [
  { path: '', component: MapComponent},
  { path: 'signin', component: SignInComponent, canActivate: [IsNotAuthGuard]},
  { path: 'signup', component: SignUpComponent, canActivate: [IsNotAuthGuard]},
  { path: 'map', component: MapComponent},
  { path: 'pois', component: PoisComponent, canActivate: [IsAuthGuard, RolesGuard], data:{roles: ['POI_OWNER', 'ADMIN']}},
  { path: 'profile', component: ProfileComponent, canActivate: [IsAuthGuard]},
  { path: 'accounts', component: AccountsComponent, canActivate: [IsAuthGuard, RolesGuard], data:{roles: ['ADMIN']}},
  { path: 'reservations', component: ReservationsComponent, canActivate: [IsAuthGuard, RolesGuard], data:{roles: ['POI_OWNER', 'ADMIN']}},
  { path: '**', component: PageNotFoundComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppRoutingModule {}
