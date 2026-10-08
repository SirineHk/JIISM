import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';

interface LoginResponse {
  token: string;
}

interface JwtPayload {
  sub?: string;
  roles?: string[];
  iat?: number;
  exp?: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://jiism-backend-jiism.apps.dell64.dottn.com/auth';

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  login(username: string, password: string): Observable<LoginResponse> {

    return this.http.post<LoginResponse>(
      `${this.apiUrl}/login`,
      {
        username,
        password
      }
    ).pipe(

      tap(response => {

        // Sauvegarder le JWT
        localStorage.setItem('token', response.token);

        // Décoder le JWT
        const payload = this.decodeToken(response.token);

        console.log('JWT payload:', payload);

        // Sauvegarder username
        if (payload?.sub) {
          localStorage.setItem('username', payload.sub);
        }

        // Sauvegarder les rôles
        if (payload?.roles) {
          localStorage.setItem(
            'roles',
            JSON.stringify(payload.roles)
          );
        }
      })
    );
  }


  private decodeToken(token: string): JwtPayload | null {

    try {

      const parts = token.split('.');

      if (parts.length !== 3) {
        return null;
      }

      const base64Url = parts[1];

      const base64 = base64Url
        .replace(/-/g, '+')
        .replace(/_/g, '/');

      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map(char =>
            '%' + ('00' + char.charCodeAt(0).toString(16)).slice(-2)
          )
          .join('')
      );

      return JSON.parse(jsonPayload);

    } catch (error) {

      console.error(
        'Erreur lors du décodage du JWT',
        error
      );

      return null;
    }
  }


  logout(): void {

    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('roles');

    this.router.navigate([
      '/authentication/login'
    ]);
  }


  isAuthenticated(): boolean {
    return !!localStorage.getItem('token');
  }


  getToken(): string | null {
    return localStorage.getItem('token');
  }


  getRoles(): string[] {

    const roles = localStorage.getItem('roles');

    if (!roles) {
      return [];
    }

    try {
      return JSON.parse(roles);
    } catch {
      return [];
    }
  }


  hasRole(role: string): boolean {

    const roles = this.getRoles();

    return roles.includes(role) ||
           roles.includes(`ROLE_${role}`);
  }


  getUsername(): string | null {
    return localStorage.getItem('username');
  }


  getPrimaryRole(): string | null {

    const roles = this.getRoles();

    if (roles.length === 0) {
      return null;
    }

    return roles[0].replace('ROLE_', '');
  }
}