import { ComponentFixture, TestBed } from '@angular/core/testing';

import { YesNoQuestionDialogComponent } from './yes-no-question.dialog.component';

describe('YesNoQuestionDialogComponent', () => {
  let component: YesNoQuestionDialogComponent;
  let fixture: ComponentFixture<YesNoQuestionDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ YesNoQuestionDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(YesNoQuestionDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
