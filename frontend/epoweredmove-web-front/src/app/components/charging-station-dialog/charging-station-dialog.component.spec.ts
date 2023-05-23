import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChargingStationDialogComponent } from './charging-station-dialog.component';

describe('ChargingStationDialogComponent', () => {
  let component: ChargingStationDialogComponent;
  let fixture: ComponentFixture<ChargingStationDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChargingStationDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChargingStationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
