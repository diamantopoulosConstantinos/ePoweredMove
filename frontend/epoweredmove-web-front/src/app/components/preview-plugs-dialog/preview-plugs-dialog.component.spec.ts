import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreviewPlugsDialogComponent } from './preview-plugs-dialog.component';

describe('PreviewPlugsDialogComponent', () => {
  let component: PreviewPlugsDialogComponent;
  let fixture: ComponentFixture<PreviewPlugsDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PreviewPlugsDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PreviewPlugsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
