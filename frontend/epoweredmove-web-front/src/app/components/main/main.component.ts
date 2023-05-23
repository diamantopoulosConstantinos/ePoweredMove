import { Component } from '@angular/core';
import {LoaderComponent} from "../loader/loader.component";
import {LoaderService} from "../../services/loader.service";

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css']
})
export class MainComponent {

  constructor(public loader: LoaderService) { }

}
