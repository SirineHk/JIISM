import { Component } from '@angular/core';
import { CoreService } from 'src/app/services/core.service';
import { CommonModule } from '@angular/common';

import {
  FormGroup,
  FormControl,
  Validators
} from '@angular/forms';

import { Router } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MaterialModule } from 'src/app/material.module';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-side-register',
  imports: [
    RouterModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,
    CommonModule
  ],
  templateUrl: './side-register.component.html',
})
export class AppSideRegisterComponent {

  options = this.settings.getOptions();

  private apiUrl =
    'http://jiism-backend-jiism.apps.dell64.dottn.com/auth/register';

  errorMessage = '';
  successMessage = '';
  loading = false;

  constructor(
    private settings: CoreService,
    private router: Router,
    private http: HttpClient
  ) {}

  form = new FormGroup({

    uname: new FormControl('', [
      Validators.required,
      Validators.minLength(6)
    ]),

    email: new FormControl('', [
      Validators.required,
      Validators.email
    ]),

    password: new FormControl('', [
      Validators.required,
      Validators.minLength(6)
    ])

  });

  get f() {
    return this.form.controls;
  }

  submit(): void {

    this.errorMessage = '';
    this.successMessage = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;

    const registerData = {
      username: this.form.value.uname,
      email: this.form.value.email,
      password: this.form.value.password
    };

    console.log('Register →', {
      username: registerData.username,
      email: registerData.email
    });

    this.http.post(
  this.apiUrl,
  registerData,
  { responseType: 'text' }
).subscribe({

  next: (response) => {

    console.log('Inscription réussie :', response);

    this.loading = false;

    this.successMessage = response;

    this.form.reset();
     this.router.navigate(['/authentication/login']);

  },

  error: (error) => {

        console.error('Erreur inscription :', error);

        this.loading = false;

        if (error.status === 400) {
          this.errorMessage =
            error.error || 'Les données saisies sont invalides.';
        }
        else if (error.status === 0) {
          this.errorMessage =
            'Impossible de contacter le serveur.';
        }
        else {
          this.errorMessage =
            'Une erreur est survenue lors de l’inscription.';
        }

      }

    });
  }
}