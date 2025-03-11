'use client'
import Swal from 'sweetalert2'
import { IProfile } from '@/src/types/IProfile'
import { useEffect, useState } from 'react'
import axios from 'axios'
import Image from 'next/image'

const Page: React.FC = () => {
  const [dataForm, setDataForm] = useState<IProfile>({
    name: '',
    userName: '',
    surname: '',
    city: '',
    language: '',
    timeZone: '',
  })

  const [isEditing, setIsEditing] = useState(false)

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const token = localStorage.getItem('accessToken')
        console.log(token)

        if (!token) {
          throw new Error('No se encontró el token de acceso')
        }

        const response = await axios.get(
          'https://equipo-c24-25-m-webapp-1.onrender.com/api/banca/users/perfil-usuario',
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        )
        setDataForm(response.data)
      } catch (error) {
        console.error('Error al obtener el perfil:', error)
      }
    }

    fetchProfile()
  }, [])

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target
    setDataForm((prevState) => ({
      ...prevState,
      [name]: value,
    }))
  }

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    try {
      await axios.put(
        'https://equipo-c24-25-m-webapp-1.onrender.com/api/banca/users/editar',
        dataForm
      )

      Swal.fire({
        title: 'Se editó correctamente',
        icon: 'success',
        draggable: true,
      })

      setIsEditing(false)
    } catch (error) {
      console.error('ERROR AL EDITAR', error)
      alert('HUBO UN ERROR AL EDITAR')
    }
  }

  return (
    <div className="flex flex-col justify-center items-center">
      <form onSubmit={handleSubmit} className="w-[90vw]">
        <div className="flex justify-between items-center mb-8">
          <div className="flex items-center">
            <Image
              src="/estafas.png"
              alt="Foto de perfil"
              className="mr-6"
              width={80}
              height={80}
            />
            <div>
              <p>
                {dataForm.name} {dataForm.surname}
              </p>
              <p>{dataForm.userName}</p>
            </div>
          </div>
          <button
            type="submit"
            onClick={() => setIsEditing(!isEditing)}
            className="w-[125px] h-[45px] bg-nav-buttons flex justify-center items-center rounded-[30px] mt-8 hover:bg-buttons-hover hover:text-white"
          >
            <p className="text-[11px]">{isEditing ? 'Cancelar' : 'Editar'}</p>
          </button>
        </div>

        <article className="flex justify-between">
          <section>
            <div className="flex flex-col mb-4">
              <label htmlFor="name" className="mb-2">
                Nombre Completo
              </label>
              <input
                type="text"
                name="name"
                id="name"
                placeholder="Nombre Completo"
                value={dataForm.name}
                readOnly={!isEditing}
                onChange={handleChange}
                className="w-[40vw] h-[39px] border-2 border-input-border rounded-md pl-4"
              />
            </div>
            <div className="flex flex-col mb-4">
              <label htmlFor="surname" className="mb-2">
                Apellido
              </label>
              <input
                type="text"
                name="surname"
                id="surname"
                placeholder="Apellido"
                value={dataForm.surname}
                readOnly={!isEditing}
                onChange={handleChange}
                className="w-[40vw] h-[39px] border-2 border-input-border rounded-md pl-4"
              />
            </div>
            <div className="flex flex-col mb-4">
              <label htmlFor="language" className="mb-2">
                Lenguaje
              </label>
              <input
                type="text"
                name="language"
                id="language"
                placeholder="Lengaje"
                value={dataForm.language}
                readOnly={!isEditing}
                onChange={handleChange}
                className="w-[40vw] h-[39px] border-2 border-input-border rounded-md pl-4"
              />
            </div>
            <div className="flex flex-col items-start mb-4">
              <label htmlFor="email" className="mb-2">
                Dirección de Correo Electronico
              </label>
              <input
                type="checkbox"
                name="email"
                id="email"
                className="w-[1em] h-[1em] border-2 border-input-border rounded-md"
              />
            </div>
          </section>

          <section>
            <div className="flex flex-col mb-4">
              <label htmlFor="userName" className="mb-2">
                Nombre de Usuario
              </label>
              <input
                type="text"
                name="userName"
                id="userName"
                onChange={handleChange}
                value={dataForm.userName}
                readOnly={!isEditing}
                placeholder="Nombre de Usuario"
                className="w-[40vw] h-[39px] border-2 border-input-border rounded-md pl-4"
              />
            </div>
            <div className="flex flex-col mb-4">
              <label htmlFor="city" className="mb-2">
                Ciudad
              </label>
              <input
                type="text"
                name="city"
                id="city"
                value={dataForm.city}
                readOnly={!isEditing}
                onChange={handleChange}
                placeholder="Ciudad"
                className="w-[40vw] h-[39px] border-2 border-input-border rounded-md pl-4"
              />
            </div>
            <div className="flex flex-col mb-2">
              <label htmlFor="timeZone" className="mb-2">
                Zona Horaria
              </label>
              <input
                type="text"
                name="timeZone"
                id="timeZone"
                value={dataForm.timeZone}
                readOnly={!isEditing}
                onChange={handleChange}
                placeholder="Zona Horaria"
                className="w-[40vw] h-[39px] border-2 border-input-border rounded-md pl-4"
              />
            </div>
          </section>
        </article>

        {isEditing && (
          <button
            type="submit"
            className="w-[125px] h-[45px] mb-10 bg-nav-buttons flex justify-center items-center rounded-[30px] mt-8 hover:bg-buttons-hover hover:text-white"
          >
            <p className="text-[11px]">Guardar Cambios</p>
          </button>
        )}

        <button
          type="submit"
          className="w-[125px] h-[45px] mb-10 bg-nav-buttons flex justify-center items-center rounded-[30px] mt-8 hover:bg-buttons-hover hover:text-white"
        >
          <p className="text-[11px]">+ Sumar mail</p>
        </button>
      </form>
    </div>
  )
}

export default Page
