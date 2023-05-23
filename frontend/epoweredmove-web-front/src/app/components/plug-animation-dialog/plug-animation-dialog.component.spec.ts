import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlugAnimationDialogComponent } from './plug-animation-dialog.component';

describe('PlugAnimationDialogComponent', () => {
  let component: PlugAnimationDialogComponent;
  let fixture: ComponentFixture<PlugAnimationDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PlugAnimationDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PlugAnimationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
