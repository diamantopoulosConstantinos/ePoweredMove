import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PoisComponent } from './pois.component';

describe('PoisComponent', () => {
  let component: PoisComponent;
  let fixture: ComponentFixture<PoisComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PoisComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PoisComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
