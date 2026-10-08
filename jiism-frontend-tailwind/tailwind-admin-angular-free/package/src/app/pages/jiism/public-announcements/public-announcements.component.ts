import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { JiismService } from '../../../services/jiism.service';

@Component({
  selector: 'app-public-announcements',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './public-announcements.component.html'
})
export class PublicAnnouncementsComponent {

  service = inject(JiismService);

  announcements: any[] = [];
  loading = true;
  error = false;

  constructor() {
    this.loadAnnouncements();
  }

  loadAnnouncements() {
    this.loading = true;
    this.error = false;

    this.service.getPublicAnnouncements().subscribe({
      next: data => {
        this.announcements = data ?? [];
        this.loading = false;
      },
      error: error => {
        console.error('Erreur récupération annonces publiques :', error);
        this.error = true;
        this.loading = false;
      }
    });
  }
}