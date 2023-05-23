import {Component, Input} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {MatIconRegistry} from "@angular/material/icon";
import {DomSanitizer} from "@angular/platform-browser";
import {UserModel} from "../../models/user.model";

@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.css']
})
export class SignUpComponent {
  signupform: FormGroup = new FormGroup({
    name: new FormControl(null, [Validators.required]),
    surname: new FormControl(null),
    phone: new FormControl(null, [Validators.required, Validators.pattern('[- +()0-9]{6,12}')]),
    email: new FormControl(null, [Validators.required, Validators.email]),
    password: new FormControl(null, [
      Validators.required,
      Validators.minLength(8),
      Validators.pattern("^.*(?=.*\\d)(?=.*[a-zA-Z])(?=.*[~?<>_!@#$%^&*]).*$")]),
    confirmPassword: new FormControl(null, [Validators.required])
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


  signUpLocal() {
    this.errors = null;
    if(this.signupform.valid){
      if(this.signupform.get('password')?.value !== this.signupform.get('confirmPassword')?.value){
        this.errors = [];
        this.errors.push("Passwords don't match");
        return;
      }
      const userCreated: UserModel = {} as UserModel;
      userCreated.email = this.signupform.get('email')?.value;
      userCreated.name = this.signupform.get('name')?.value;
      userCreated.surname = this.signupform.get('surname')?.value;
      userCreated.phone = this.signupform.get('phone')?.value;
      userCreated.password = this.signupform.get('password')?.value;
      this.authService.createLocalUser(userCreated);
    }
    else{
      this.errors = [];
      if(this.signupform.get('name')?.hasError('required')){
        this.errors.push("Name required");
      }

      if(this.signupform.get('email')?.hasError('required')){
        this.errors.push("Email required");
      }
      else if(this.signupform.get('email')?.hasError('email')){
        this.errors.push("Not valid email");
      }

      if(this.signupform.get('phone')?.hasError('required')){
        this.errors.push("Phone required");
      }
      else if(this.signupform.get('phone')?.hasError('pattern')){
        this.errors.push("Not valid phone number]");
      }

      if(this.signupform.get('password')?.hasError('required')){
        this.errors.push("Password required");
      }
      else if(this.signupform.get('password')?.hasError('minlength')){
        this.errors.push("Password must be more than 8 characters");
      }
      else if(this.signupform.get('password')?.hasError('pattern')){
        this.errors.push("Password must contain at least one of number, character and symbol");
      }
      else if(this.signupform.get('confirmPassword')?.hasError('required')){
        this.errors.push("Confirm Password");
      }
    }
  }

  logInGoogle() {
    this.authService.loginGoogle();
  }
}
