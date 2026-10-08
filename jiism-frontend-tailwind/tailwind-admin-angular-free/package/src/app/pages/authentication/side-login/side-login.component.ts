import { Component, inject } from '@angular/core';
import {
  FormGroup,
  FormControl,
  Validators,
  ReactiveFormsModule
} from '@angular/forms';

import {
  Router,
  RouterModule
} from '@angular/router';

import { CommonModule } from '@angular/common';

import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-side-login',
  standalone: true,
  imports: [
    RouterModule,
    ReactiveFormsModule,
    CommonModule
  ],
  templateUrl: './side-login.component.html'
})
export class AppSideLoginComponent {

  private authService = inject(AuthService);
  private router = inject(Router);

  loading = false;
  error = '';

  form = new FormGroup({

    uname: new FormControl('', [
      Validators.required
    ]),

    password: new FormControl('', [
      Validators.required
    ])

  });


  submit(): void {

    if (this.form.invalid) {

      this.form.markAllAsTouched();

      return;
    }


    this.loading = true;
    this.error = '';


    const username = this.form.value.uname ?? '';
    const password = this.form.value.password ?? '';


    this.authService.login(
      username,
      password
    ).subscribe({

      // ==========================================
      // LOGIN RÉUSSI
      // ==========================================

      next: () => {

        this.loading = false;

        const roles =
          this.authService.getRoles();

        console.log(
          'Utilisateur connecté :',
          this.authService.getUsername()
        );

        console.log(
          'Rôles :',
          roles
        );


        // ========================================
        // ADMIN
        // ========================================

        if (roles.includes('ROLE_ADMIN')) {
  this.router.navigate(['/admin/dashboard']);
  return;
}

if (roles.includes('ROLE_EDITEUR')) {
  this.router.navigate(['/editor/dashboard']);
  return;
}

if (roles.includes('ROLE_AUTEUR')) {
  this.router.navigate(['/author/dashboard']);
  return;
}


        // ========================================
        // ROLE INCONNU
        // ========================================

        this.error =
          'Aucun rôle valide n’a été trouvé pour ce compte.';

        this.authService.logout();
      },


      // ==========================================
      // LOGIN ÉCHOUÉ
      // ==========================================

      error: (error) => {

        console.error(
          'Erreur de connexion :',
          error
        );

        this.loading = false;

        this.error =
          'Identifiants invalides ou serveur indisponible.';
      }

    });
  }
}