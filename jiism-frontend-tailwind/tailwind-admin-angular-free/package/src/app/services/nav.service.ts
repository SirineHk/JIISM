import { Injectable, signal } from '@angular/core';
import { Event, NavigationEnd, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { NavItem } from '../layouts/full/sidebar/nav-item/nav-item';

@Injectable({
  providedIn: 'root'
})
export class NavService {

  showClass: any = false;

  public currentUrl = signal<string | undefined>(undefined);

  constructor(
    private router: Router,
    private authService: AuthService
  ) {

    this.router.events.subscribe((event: Event) => {

      if (event instanceof NavigationEnd) {
        this.currentUrl.set(event.urlAfterRedirects);
      }

    });

  }

  getCurrentRole(): string | null {
    return this.authService.getPrimaryRole();
  }

  getNavItems(items: NavItem[]): NavItem[] {

    const role = this.getCurrentRole();

    return items.filter(item => {

      if (!item.roles) {
        return false;
      }

      if (item.roles.includes('ALL')) {
        return true;
      }

      if (!role) {
        return false;
      }

      return item.roles.includes(role);
    });
  }
}