package com.droidcon.habitsync.utils

import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.BaseRealmObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

fun <T : BaseRealmObject> RealmResults<T>?.toResultFlow(): Flow<Result<List<T>>> {
    return this?.asFlow()
        ?.map { queryResult ->
            val items = queryResult.list
            if (items.isNotEmpty()) {
                Result.success(items)
            } else {
                Result.failure(Exception("No data found"))
            }
        } ?: flowOf(Result.failure(Exception("Database error")))
}
enum class ViewStatus{
    INITIAL, LOADING, SUCCESS, FAILED
}