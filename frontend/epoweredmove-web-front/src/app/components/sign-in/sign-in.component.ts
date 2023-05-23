import {Component, Input} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {MatIconRegistry} from "@angular/material/icon";
import {DomSanitizer} from "@angular/platform-browser";
import {FloatLabelType} from "@angular/material/form-field";

@Component({
  selector: 'app-sign-in',
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.css']
})
export class SignInComponent {

  signinform: FormGroup = new FormGroup({
    email: new FormControl(null, [Validators.required, Validators.email]),
    password: new FormControl(null, [
      Validators.required,
      Validators.minLength(8),
      Validators.pattern("^.*(?=.*\\d)(?=.*[a-zA-Z])(?=.*[~?<>_!@#$%^&*]).*$")]),
  });

  @Input() errors: string[] | null = null;
  constructor(private authService: AuthService,
              private matIconRegistry: MatIconRegistry,
              private domSanitizer: DomSanitizer) {
    //add custom svg icon
    this.matIconRegistry.addSvgIcon(
      "google",
      this.domSanitizer.bypassSecurityTrustResourceUrl("../assets/svg/google.svg")
    );
  }

  logInLocal() {
    this.errors = null;
    if(this.signinform.valid){
      const email = this.signinform.get('email')?.value;
      const password = this.signinform.get('password')?.value;
      this.authService.login(email, password);
    }
    else{
      this.errors = [];
      if(this.signinform.get('email')?.hasError('required')){
        this.errors.push("Email required");
      }
      else if(this.signinform.get('email')?.hasError('email')){
        this.errors.push("Not valid email");
      }

      else if(this.signinform.get('password')?.hasError('minlength')){
        this.errors.push("Password must be more than 8 characters");
      }
      else if(this.signinform.get('password')?.hasError('pattern')){
        this.errors.push("Password must contain at least one of number, character and symbol");
      }
      else if(this.signinform.get('password')?.hasError('pattern')){
        this.errors.push("Not valid email");
      }
    }
  }

  logInGoogle() {
    this.authService.loginGoogle();
  }
}
