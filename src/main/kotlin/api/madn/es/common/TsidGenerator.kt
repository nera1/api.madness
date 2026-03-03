package api.madn.es.common

import com.github.f4b6a3.tsid.TsidCreator

object TsidGenerator {
    fun next(): Long = TsidCreator.getTsid().toLong()
}
