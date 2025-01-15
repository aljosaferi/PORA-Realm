# Realm

## :thinking:Zakaj? 

Realm je moderna baza podatkov, namenjena mobilnim aplikacijam. Omogoča hitro in enostavno shranjevanje podatkov lokalno na napravi ter podpira real-time posodobitve. 
Zaradi objektno-orientiranega pristopa je delo z njo preprosto, saj ni potrebe po kompleksnih SQL poizvedbah. Z uporabo Realm Sync lahko podatke sinhroniziramo med lokalno napravo in mongodb oblakom.
Realm je poznan po visoki zmogljivosti in nizki porabi virov, kar ga naredi idealnega za mobilne aplikacije.

## :heavy_check_mark:Prednosti

- preprosta uporaba
- omogoča hitro shranjevanje in dostopnaje do podatkov
- samodejno posodabljanje pdoatkov v realnem času
- z Realm Sync lahko sinhroniziramo z oblakom (end of life 30. september 2025)
- podpira android, react native, nodejs, javascript...
- objektno orientiran pristop, podpira vsebovanje(vsebovanje)

## :x:Slabosti

- omejena podpora za napredne poizvedbe, podpira bolj preproste
- manjša skupnost razvijalcev
- minimalno vzdrževanje
- Atlas Device Sync bo od 30. septembra 2025 deprecated, lokalna baza bo ostala kot open source projekt
- dedovanje ni točno podprto, lahko ga delno implementiram osami
- ne moremo direktno pogledat elementov v bazi, lahko jih loggamo v konzolo, ali prikažemo v aplikaciji

## :notebook_with_decorative_cover:Licenca

[Apache-2.0 license](https://www.apache.org/licenses/)

Apache licenca je odprtokodna programska licenca, ki uporabnikom omogoča prosto uporabo, spreminjanje in distribucijo programske opreme z minimalnimi omejitvami.
Prav tako zagotavlja izrecno podelitev pravic do patentov s strani prispevalcev uporabnikom, kar jih ščiti pred tožbami v zvezi s patenti, povezanimi s programsko opremo.

## Število zvezdic, sledilcev, forkov
Realm:

![GitHub stars](https://img.shields.io/github/stars/realm/realm-java?style=social)

![GitHub watchers](https://img.shields.io/github/watchers/realm/realm-java?style=social)

![GitHub forks](https://img.shields.io/github/forks/realm/realm-java?style=social)

Realm-Kotlin (android studio):

⭐Zvezdice: 985

👥Watchers: 28

🍴Forks: 62

## Vzdrževanje projekta

- **Zadnji commit** - 3. 10. 2024
- **Contributors** - 30
- Knjižnica bo deprecated od 30. septembra naprej, lokalna baza bo ostala kot open source projekt

## Primeri uporabe

### Vključitev

V global build.gradle vključimo plugin in dependency
```kt
plugins {
   id 'io.realm.kotlin' version '1.16.0' apply false
}

dependencies {
    implementation("io.realm.kotlin:library-base:1.16.0")
}
```

### Povezava na bazo in uporaba
```kt
class SimpleDemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySimpleDemoBinding
    private lateinit var realm: Realm
    ...

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySimpleDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Realm
        val config = RealmConfiguration.create(schema = setOf(PersonRealm::class, StudentRealm::class))
        realm = Realm.open(config)

        people = realm.query(PersonRealm::class).find()
```

V config navedemo katere sheme(razrede) bomo uporabljali, naprimer v tem primeru sta to person in student, realm nato odpremo s to konfiguracijo, če sheme ne navedemo, ne moremo delati z njo.
Z ```realm.query(PersonRealm::class).find()``` bomo iz baze potegnili vse elemente razreda PersonRealm

```kt
open class PersonRealm : RealmObject  {
    @PrimaryKey
    var _id: String = UUID.randomUUID().toString()
    var name: String = ""
    var city: String = ""
}

open class StudentRealm : RealmObject {
    @PrimaryKey
    var _id: String = UUID.randomUUID().toString()

    var person: PersonRealm? = null

    var studentId: String = ""
    var grade: Int = 0
}
```

```PersonRealm``` je razred, ki predstavlja osnovno osebo v aplikaciji. To je enostavna entiteta, ki shranjuje osnovne podatke o osebi, kot so njeno ime in mesto.
- ```@PrimaryKey``` Ta anotacija označuje, da je _id glavni ključ te entitete, kar pomeni, da mora biti vsak objekt PersonRealm edinstven in bo identificiran z _id.
- ```_id``` Gre za unikatni ID osebe, ki se generira z uporabo UUID.randomUUID() in je privzeto dodeljen ob ustvarjanju objekta.
- ```name:``` Shranjuje ime osebe.
- ```city:``` Shranjuje mesto, v katerem oseba živi.

```StudentRealm``` je razred, ki pridobi osnovno entiteto osebe (prek vsebovanja) in doda lastnosti, ki so specifične za študenta, kot so študentska številka in ocena.
- ```@PrimaryKey:``` Podobno kot pri PersonRealm, je _id tudi za StudentRealm glavna identifikacija objekta.
- ```person:``` Ta lastnost je povezava na objekt PersonRealm, ki predstavlja osebo, povezano s študentom. To pomeni, da vsak študent "podeduje" osnovne informacije o osebi, kot so ime in mesto.
- ```studentId:``` Shranjuje študentsko številko, ki je specifična za študenta.
- ```grade:``` Shranjuje oceno študenta.

### Dodajanje v bazo

To omogoča dodajanje novih oseb v bazo, kjer se ustvarijo naključno ime in mesto ter se shrani v bazo.

```kt
    fun addPerson() {
        val nameRand = names.random()
        val cityRand = cities.random()

        realm.writeBlocking {
            copyToRealm(PersonRealm().apply {
                _id = UUID.randomUUID().toString()
                name = nameRand
                city = cityRand
            })
        }
        logDatabaseContent()
    }
```


### Odstranjevanej iz baze

Odstranitev osebe iz baze, kjer najprej pridobimo frozen objekt, nato pa najdemo najnovejšo različico objekta in ga izbrišemo iz baze. Tukaj je odstranjevanje na podlagi indeksa, lahko pa tudi po id

```kt
    fun removePersonAtIndex(index: Int) {
        realm.writeBlocking {
            val frozenPerson = people[index]
            val livePerson = findLatest(frozenPerson)
            livePerson?.let { delete(it) }
        }
        logDatabaseContent()
    }
```
Primer kode iz 2 naloge pri PORA, kjer je odstranjevanje preko querya
```kt
    fun removeActivity(activityToDelete: Activity) {
        activityManager.activityList.remove(activityToDelete)
        realm.writeBlocking {
            val activity = query<ActivityRealm>(ActivityRealm::class,"_id == $0", activityToDelete.uuid.toString()).first().find()
            activity?.let { delete(it) }
        }
    }
```

### Posodabljanje

Posodabljanje osebe v bazi, kjer iskanje temelji na ID-ju in potem spremeni vrednosti imena in mesta.

```kt
    fun updatePerson() {
        realm.writeBlocking {
            val activity = query<PersonRealm>(PersonRealm::class, "_id == $0", "b1897bc9-3acb-4b0b-9104-a603e0f73ded").first().find()
            activity?.apply {
                name = "Aljoša"
                city = "Maribor"
            }
        }
    }
```

### Prikaz vsebine baze

Ta funkcija preprosto izpiše vsebino baze v log, kar omogoča spremljanje trenutnih oseb v bazi.

```kt
fun logDatabaseContent() {
        realm.writeBlocking {
            val people = query(PersonRealm::class).find()

            if (people.isNotEmpty()) {
                // Log each person's details
                for (person in people) {
                    Log.d("RealmData", "Person: ID: ${person._id}, Name: ${person.name}, City: ${person.city}")
                }
            } else {
                Log.d("RealmData", "No persons found in the database.")
            }
        }
    }
```

<img width="894" alt="Capture" src="https://github.com/user-attachments/assets/7d808be9-451e-46ed-b64e-98d415193326" />

<img width="1058" alt="Capture2" src="https://github.com/user-attachments/assets/fb258254-7b60-4823-a4f6-1cb24b748563" />

Prikaz preko RecyclerView

<img width="177" alt="Capture1" src="https://github.com/user-attachments/assets/f58e994e-b70e-4ebb-9fcf-b441d020a736" />

## Vključitev v nalogo

```kt
class ActivityRealm() : RealmObject {
    @PrimaryKey
    var _id: String = UUID.randomUUID().toString()
    var name: String = ""
    var duration: String = ""
    var date: String = ""
    var weatherCode: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var selectedImage: String = ""
}
```


<img width="219" alt="Capture3" src="https://github.com/user-attachments/assets/615ed93f-e827-480f-a07e-d9d1cc751d44" />

