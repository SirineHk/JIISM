import { inject } from '@angular/core';
import {
  CanActivateFn,
  Router
} from '@angular/router';

import { AuthService } from '../services/auth.service';

export const roleGuard = (
  allowedRoles: string[]
): CanActivateFn => {

  return () => {

    const authService = inject(AuthService);
    const router = inject(Router);

    if (!authService.isAuthenticated()) {
      return router.createUrlTree([
        '/authentication/login'
      ]);
    }

    const userRoles = authService.getRoles();

    console.log('Rôles utilisateur :', userRoles);
    console.log('Rôles autorisés :', allowedRoles);

    const hasRole = allowedRoles.some(role =>
      userRoles.includes(role) ||
      userRoles.includes(`ROLE_${role}`)
    );

    if (hasRole) {
      return true;
    }

    const primaryRole = authService.getPrimaryRole();

    console.warn(
      'Accès refusé. Rôle utilisateur :',
      primaryRole
    );

    if (primaryRole === 'ADMIN') {
      return router.createUrlTree([
        '/admin/dashboard'
      ]);
    }

    if (primaryRole === 'EDITEUR') {
      return router.createUrlTree([
        '/editor/dashboard'
      ]);
    }

    if (primaryRole === 'AUTEUR') {
      return router.createUrlTree([
        '/author/dashboard'
      ]);
    }

    return router.createUrlTree([
      '/authentication/login'
    ]);
  };
};