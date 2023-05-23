import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreviewPlugDialogComponent } from './preview-plug-dialog.component';

describe('PreviewPlugDialogComponent', () => {
  let component: PreviewPlugDialogComponent;
  let fixture: ComponentFixture<PreviewPlugDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PreviewPlugDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PreviewPlugDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
