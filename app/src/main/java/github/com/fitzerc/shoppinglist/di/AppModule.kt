package github.com.fitzerc.shoppinglist.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import github.com.fitzerc.shoppinglist.data.access.room.EntryDao
import github.com.fitzerc.shoppinglist.data.access.room.ListDao
import github.com.fitzerc.shoppinglist.data.access.room.ShoppingListDatabase
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Singleton
    @Provides
    fun ProvideListDao(listDb: ShoppingListDatabase): ListDao = listDb.listDao()

    @Singleton
    @Provides
    fun ProvideEntryDao(db: ShoppingListDatabase): EntryDao = db.entryDao()

    @Singleton
    @Provides
    fun ProvideAppDatabase(@ApplicationContext context: Context): ShoppingListDatabase
    = Room.databaseBuilder(
        context,
        ShoppingListDatabase::class.java,
        "shoppinglist_db"
    ).fallbackToDestructiveMigration().build()
}