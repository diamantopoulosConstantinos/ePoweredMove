import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PoiAnimationDialogComponent } from './poi-animation-dialog.component';

describe('PoiAnimationDialogComponent', () => {
  let component: PoiAnimationDialogComponent;
  let fixture: ComponentFixture<PoiAnimationDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PoiAnimationDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PoiAnimationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
