import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

interface Utilisateur {
  id: number;
  username: string;
  email: string;
  password?: string;
  firstName: string;
  lastName: string;
  phone?: string;
  roles: string[];
}

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './users.component.html'
})
export class UsersComponent implements OnInit {

  private apiUrl =
    'http://jiism-backend-jiism.apps.dell64.dottn.com/admin/users';

  users: Utilisateur[] = [];

  loading = true;
  error = '';

  // Modal
  showModal = false;
  editMode = false;

  // Utilisateur en cours d'ajout/modification
  selectedUserId: number | null = null;

  formUser: Utilisateur = this.emptyUser();

  roles = [
    'ADMIN',
    'AUTEUR',
    'EDITEUR',
    'PUBLIC'
  ];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  // =========================================================
  // CHARGER LES UTILISATEURS
  // =========================================================

  loadUsers(): void {

    this.loading = true;
    this.error = '';

    this.http.get<Utilisateur[]>(this.apiUrl).subscribe({

      next: (data) => {

        console.log('Utilisateurs reçus :', data);

        this.users = data;

        this.loading = false;
      },

      error: (err) => {

        console.error(
          'Erreur lors du chargement des utilisateurs :',
          err
        );

        this.error =
          'Impossible de charger les utilisateurs.';

        this.loading = false;
      }
    });
  }


  // =========================================================
  // UTILISATEUR VIDE
  // =========================================================

  emptyUser(): Utilisateur {

    return {
      id: 0,
      username: '',
      email: '',
      password: '',
      firstName: '',
      lastName: '',
      phone: '',
      roles: ['AUTEUR']
    };
  }


  // =========================================================
  // OUVRIR AJOUT
  // =========================================================

  openAddModal(): void {

    this.editMode = false;
    this.selectedUserId = null;

    this.formUser = this.emptyUser();

    this.showModal = true;
  }


  // =========================================================
  // OUVRIR MODIFICATION
  // =========================================================

  openEditModal(user: Utilisateur): void {

    this.editMode = true;
    this.selectedUserId = user.id;

    this.formUser = {
      id: user.id,
      username: user.username,
      email: user.email,
      password: '',
      firstName: user.firstName || '',
      lastName: user.lastName || '',
      phone: user.phone || '',
      roles: [...(user.roles || ['AUTEUR'])]
    };

    this.showModal = true;
  }


  // =========================================================
  // FERMER MODAL
  // =========================================================

  closeModal(): void {

    this.showModal = false;
    this.formUser = this.emptyUser();
    this.selectedUserId = null;
  }


  // =========================================================
  // AJOUTER / MODIFIER
  // =========================================================

  saveUser(): void {

    if (!this.formUser.username.trim()
        || !this.formUser.email.trim()) {

      alert('Username et email sont obligatoires.');
      return;
    }

    // AJOUT
    if (!this.editMode) {

      if (!this.formUser.password?.trim()) {

        alert('Le mot de passe est obligatoire.');
        return;
      }

      const userToCreate = {
        username: this.formUser.username,
        email: this.formUser.email,
        password: this.formUser.password,
        firstName: this.formUser.firstName,
        lastName: this.formUser.lastName,
        phone: this.formUser.phone,
        roles: this.formUser.roles
      };

      this.http.post(this.apiUrl, userToCreate).subscribe({

        next: () => {

          alert('Utilisateur ajouté avec succès.');

          this.closeModal();
          this.loadUsers();
        },

        error: (err) => {

          console.error(
            'Erreur ajout utilisateur :',
            err
          );

          alert(
            err.error ||
            'Erreur lors de l’ajout de l’utilisateur.'
          );
        }
      });

      return;
    }


    // MODIFICATION
    if (this.selectedUserId !== null) {

      const userToUpdate: any = {
        username: this.formUser.username,
        email: this.formUser.email,
        firstName: this.formUser.firstName,
        lastName: this.formUser.lastName,
        phone: this.formUser.phone
      };

      // Envoyer le mot de passe uniquement
      // si l'admin en a saisi un nouveau.
      if (this.formUser.password?.trim()) {
        userToUpdate.password = this.formUser.password;
      }

      this.http.put(
        `${this.apiUrl}/${this.selectedUserId}`,
        userToUpdate
      ).subscribe({

        next: () => {

          alert('Utilisateur modifié avec succès.');

          this.closeModal();
          this.loadUsers();
        },

        error: (err) => {

          console.error(
            'Erreur modification utilisateur :',
            err
          );

          alert(
            err.error ||
            'Erreur lors de la modification.'
          );
        }
      });
    }
  }


  // =========================================================
  // CHANGER ROLE
  // =========================================================

  changeRole(user: Utilisateur, role: string): void {

    if (!role) {
      return;
    }

    this.http.put(
      `${this.apiUrl}/${user.id}/role`,
      {},
      {
        params: {
          role: role
        }
      }
    ).subscribe({

      next: () => {

        alert(
          `Le rôle de ${user.username} a été changé en ${role}.`
        );

        this.loadUsers();
      },

      error: (err) => {

        console.error(
          'Erreur changement rôle :',
          err
        );

        alert(
          err.error ||
          'Erreur lors du changement de rôle.'
        );

        this.loadUsers();
      }
    });
  }


  // =========================================================
  // SUPPRIMER
  // =========================================================

  deleteUser(user: Utilisateur): void {

    const confirmed = confirm(
      `Voulez-vous vraiment supprimer l'utilisateur "${user.username}" ?`
    );

    if (!confirmed) {
      return;
    }

    this.http.delete(
      `${this.apiUrl}/${user.id}`
    ).subscribe({

      next: () => {

        alert('Utilisateur supprimé avec succès.');

        this.loadUsers();
      },

      error: (err) => {

        console.error(
          'Erreur suppression utilisateur :',
          err
        );

        alert(
          err.error ||
          'Erreur lors de la suppression.'
        );
      }
    });
  }
}
