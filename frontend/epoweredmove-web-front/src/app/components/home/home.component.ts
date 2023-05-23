import {Component, OnInit} from '@angular/core';
import {LoaderComponent} from "../loader/loader.component";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit{
  constructor(private loader: LoaderComponent) {
  }

  ngOnInit(): void {
    this.loader
  }
}
