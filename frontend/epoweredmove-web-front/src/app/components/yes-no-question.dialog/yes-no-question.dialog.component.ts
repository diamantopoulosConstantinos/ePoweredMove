import {AfterViewInit, Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {PoiModel} from "../../models/poi.model";

@Component({
  selector: 'yes-no-question.dialog',
  templateUrl: './yes-no-question.dialog.component.html',
  styleUrls: ['./yes-no-question.dialog.component.css']
})
export class YesNoQuestionDialogComponent implements OnInit{
  question: string | null = null;

  constructor(public dialogRef: MatDialogRef<YesNoQuestionDialogComponent>,
              @Inject(MAT_DIALOG_DATA) private data: string) {}

  ngOnInit() {
    this.question = this.data;
  }

  answer(response: boolean) {
    this.dialogRef.close(response);
  }
}
