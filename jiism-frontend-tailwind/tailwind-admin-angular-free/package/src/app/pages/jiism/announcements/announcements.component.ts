import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { JiismService } from '../../../services/jiism.service';

@Component({
  selector: 'app-announcements',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './announcements.component.html'
})
export class AnnouncementsComponent {

  service = inject(JiismService);

  items: any[] = [];

  showForm = false;
  saving = false;

  form = {
    title: '',
    content: ''
  };

  constructor() {
    this.loadAnnouncements();
  }

  loadAnnouncements() {
    this.service.getAnnouncements().subscribe({
      next: data => {
        this.items = data;
      },
      error: error => {
        console.error(
          'Erreur récupération annonces :',
          error
        );
      }
    });
  }

  openForm() {
    this.form = {
      title: '',
      content: ''
    };

    this.showForm = true;
  }

  closeForm() {
    this.showForm = false;
  }

  createAnnouncement() {

    if (!this.form.title.trim()) {
      alert('Veuillez saisir un titre.');
      return;
    }

    if (!this.form.content.trim()) {
      alert('Veuillez saisir le contenu de l’annonce.');
      return;
    }

    this.saving = true;

    this.service.createAnnouncement({
      title: this.form.title.trim(),
      content: this.form.content.trim()
    }).subscribe({

      next: () => {

        alert('Annonce créée avec succès.');

        this.saving = false;
        this.showForm = false;

        this.loadAnnouncements();
      },

      error: error => {

        console.error(
          'Erreur création annonce :',
          error
        );

        this.saving = false;

        alert(
          error?.error?.message ||
          'Impossible de créer l’annonce.'
        );
      }

    });
  }

  publishAnnouncement(id: number) {

  this.service.publishAnnouncement(id).subscribe({

    next: () => {

      alert('Annonce publiée avec succès.');

      this.loadAnnouncements();
    },

    error: error => {

      console.error(
        'Erreur publication annonce :',
        error
      );

      alert(
        error?.error?.message ||
        'Impossible de publier l’annonce.'
      );
    }

  });
}

unpublishAnnouncement(id: number) {

  this.service.unpublishAnnouncement(id).subscribe({

    next: () => {

      alert('Annonce dépubliée avec succès.');

      this.loadAnnouncements();
    },

    error: error => {

      console.error(
        'Erreur dépublication annonce :',
        error
      );

      alert(
        error?.error?.message ||
        'Impossible de dépublier l’annonce.'
      );
    }

  });
}

deleteAnnouncement(id: number) {

  const confirmed = confirm(
    'Voulez-vous vraiment supprimer cette annonce ?'
  );

  if (!confirmed) {
    return;
  }

  this.service.deleteAnnouncement(id).subscribe({

    next: () => {

      alert('Annonce supprimée avec succès.');

      this.loadAnnouncements();
    },

    error: error => {

      console.error(
        'Erreur suppression annonce :',
        error
      );

      alert(
        error?.error?.message ||
        'Impossible de supprimer l’annonce.'
      );
    }

  });
}
}